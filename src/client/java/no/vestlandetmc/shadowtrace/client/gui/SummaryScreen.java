package no.vestlandetmc.shadowtrace.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import no.vestlandetmc.shadowtrace.client.handlers.Block;
import no.vestlandetmc.shadowtrace.client.handlers.DataManager;

import java.util.*;

public class SummaryScreen extends Screen {

	private final HashMap<String, ButtonWidget> buttonWidgets = new HashMap<>();
	private ButtonWidget btnReset;

	public SummaryScreen() {
		super(Text.translatable("shadowtrace.screen.summary.title"));
	}

	@Override
	public void init() {
		super.init();

		btnReset = ButtonWidget.builder(Text.translatable("shadowtrace.screen.summary.btn_reset_blocks"), (btn) -> {
			MinecraftClient.getInstance().setScreen(null);
			DataManager.clearBlocks();
		}).dimensions(-100, -100, 120, 18).build();
		this.addDrawableChild(btnReset);

		buttonWidgets.clear();
		for (String blockName : DataManager.getBlocks().keySet()) {
			final ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("shadowtrace.screen.summary.btn_blocks"), (btn) -> {
				final Screen currentScreen = MinecraftClient.getInstance().currentScreen;
				client.setScreen(new BlockScreen(blockName, currentScreen));
			}).dimensions(-100, -100, 60, 18).build();
			buttonWidgets.put(blockName, buttonWidget);
			this.addDrawableChild(buttonWidget);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		final int centerX = this.width / 2;
		final int tableWidth = 580;
		final int startX = centerX - tableWidth / 2;
		final int startY = 20;
		final int colorWhite = 0xFFFFFFFF;

		final MutableText noDataTxt = Text.translatable("shadowtrace.screen.summary.no_data");
		final MutableText itemTxt = Text.translatable("shadowtrace.screen.summary.item");
		final MutableText minedTxt = Text.translatable("shadowtrace.screen.summary.mined");

		final int centerTextX = centerX - (textRenderer.getWidth(noDataTxt) / 2);

		if (DataManager.getBlocks().isEmpty()) {
			context.drawText(this.textRenderer, noDataTxt, centerTextX, startY, colorWhite, true);
		} else {
			final List<String> sortedBlockNames = new ArrayList<>(DataManager.getBlocks().keySet());
			Collections.sort(sortedBlockNames);

			btnReset.setPosition(startX + tableWidth - 122, startY - 8);
			context.drawText(this.textRenderer, itemTxt, startX, startY, colorWhite, true);
			context.drawText(this.textRenderer, minedTxt, startX + 260, startY, colorWhite, true);

			int yOffset = 20;
			int textHeight = this.textRenderer.fontHeight;
			int verticalPadding = (yOffset - textHeight) / 2;
			int i = 0;

			for (String blockName : sortedBlockNames) {
				final Block entry = DataManager.getBlock(blockName);
				final int y = startY + (i + 1) * yOffset;
				final String formattedName = formatName(entry.getName());

				final boolean isHovered = mouseX >= startX - 2 && mouseX <= startX + tableWidth &&
						mouseY >= y - verticalPadding && mouseY <= y + yOffset - verticalPadding;

				final int rowColor = isHovered ? 0x80727271 : (i % 2 == 0 ? 0x80444444 : 0x80555555);
				context.fill(startX - 2, y - verticalPadding, startX + tableWidth, y + yOffset - verticalPadding, rowColor);

				final Item item = Registries.ITEM.get(Identifier.of("minecraft:" + entry.getName().toLowerCase()));
				final ItemStack itemStack = new ItemStack(item);
				context.drawItem(itemStack, startX, y - 4);

				context.drawText(this.textRenderer, formattedName, startX + 20, y, colorWhite, true);
				context.drawText(this.textRenderer, String.valueOf(entry.getBroken()), startX + 260, y, colorWhite, true);

				final ButtonWidget buttonWidget = buttonWidgets.get(blockName);
				if (buttonWidget != null) {
					buttonWidget.setPosition(startX + tableWidth - 62, y - 4);
				}

				i++;
			}
		}

		super.render(context, mouseX, mouseY, delta);
	}

	private String formatName(String input) {
		final String[] words = input.toLowerCase().split("_");
		final StringBuilder result = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			if (!word.isEmpty()) {
				result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));

				if (i < words.length - 1) {
					result.append(" ");
				}
			}
		}

		return result.toString();
	}
}
