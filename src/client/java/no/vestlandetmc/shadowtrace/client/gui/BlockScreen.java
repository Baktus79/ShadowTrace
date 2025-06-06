package no.vestlandetmc.shadowtrace.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import no.vestlandetmc.shadowtrace.client.handlers.Block;
import no.vestlandetmc.shadowtrace.client.handlers.DataManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockScreen extends Screen {

	private static final int ROW_HEIGHT = 20;
	private final String blockName;
	private final Screen parent;
	private final HashMap<BlockPos, ButtonWidget> buttonWidgets = new HashMap<>();
	private LinkedHashMap<BlockPos, Long> sortedList;
	private int scrollOffset = 0;

	public BlockScreen(String blockName, Screen parent) {
		super(Text.translatable("shadowtrace.screen.blockdata.title"));
		this.blockName = blockName;
		this.parent = parent;
	}

	@Override
	public void init() {
		super.init();

		final Block block = DataManager.getBlock(blockName);
		if (block == null) return;

		sortedList = block.getBlockPositions().entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);

		buttonWidgets.clear();
		for (BlockPos blockPos : block.getBlockPositions().keySet()) {
			if (blockPos == null || blockPos.equals(BlockPos.ORIGIN)) continue;
			ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("shadowtrace.screen.blockdata.teleport"),
					(btn) -> teleport(blockPos)).dimensions(-100, -100, 60, 18).build();
			buttonWidgets.put(blockPos, buttonWidget);
			this.addDrawableChild(buttonWidget);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		final int centerX = this.width / 2;
		final int tableWidth = 580;
		final int startX = centerX - tableWidth / 2;
		final int startY = 20;

		context.drawText(this.textRenderer, Text.translatable("shadowtrace.screen.blockdata.time"), startX, startY, 0xFFFFFF, true);
		context.drawText(this.textRenderer, Text.translatable("shadowtrace.screen.blockdata.coordinate"), startX + 130, startY, 0xFFFFFF, true);

		int yOffset = 20;
		int textHeight = this.textRenderer.fontHeight;
		int verticalPadding = (yOffset - textHeight) / 2;
		int i = 0;

		final Block block = DataManager.getBlock(blockName);
		if (block == null) return;

		for (Map.Entry<BlockPos, Long> entry : sortedList.entrySet()) {
			final int y = startY + (i + 1) * yOffset + scrollOffset;
			final BlockPos blockPos = entry.getKey();

			if (y > startY && y < this.height - ROW_HEIGHT) {
				final MutableText coordinate = Text.translatable(
						"shadowtrace.screen.blockdata.world",
						block.getWorld(),
						blockPos.getX(),
						blockPos.getY(),
						blockPos.getZ());

				final String time = convertTimestamp(entry.getValue());

				final boolean isHovered = mouseX >= startX - 2 && mouseX <= startX + tableWidth &&
						mouseY >= y - verticalPadding && mouseY <= y + yOffset - verticalPadding;

				final int rowColor = isHovered ? 0x80727271 : (i % 2 == 0 ? 0x80444444 : 0x80555555);
				context.fill(startX - 2, y - verticalPadding, startX + tableWidth, y + yOffset - verticalPadding, rowColor);

				context.drawText(this.textRenderer, time, startX, y, 0xFFFFFF, true);
				context.drawText(this.textRenderer, coordinate, startX + 130, y, 0xFFFFFF, true);

				final ButtonWidget buttonWidget = buttonWidgets.get(blockPos);
				if (buttonWidget != null) {
					buttonWidget.setPosition(startX + tableWidth - 62, y - 4);
				}
			} else {
				final ButtonWidget buttonWidget = buttonWidgets.get(blockPos);
				if (buttonWidget != null) {
					buttonWidget.setPosition(-100, -100);
				}
			}
			i++;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		final int scrollValue = (int) (verticalAmount * 20);
		int maxScroll = Math.max(0, ((sortedList.size() + 1) * ROW_HEIGHT) - (this.height - 40));
		if ((scrollOffset + scrollValue) <= 0 && Math.abs(scrollOffset + scrollValue) <= maxScroll) {
			scrollOffset += scrollValue;
		}
		return true;
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	public String convertTimestamp(long timestamp) {
		final ZoneId zoneId = ZoneId.systemDefault();
		final ZonedDateTime dateTime = Instant.ofEpochSecond(timestamp).atZone(zoneId);
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		return dateTime.format(formatter);
	}

	public void teleport(BlockPos blockPos) {
		final MinecraftClient client = MinecraftClient.getInstance();

		if (client != null && client.player != null) {
			final String teleportCommand = "shadowtrace teleport " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
			client.player.networkHandler.sendCommand(teleportCommand);
		}
	}
}
