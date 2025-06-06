package no.vestlandetmc.shadowtrace.client;

import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import no.vestlandetmc.shadowtrace.client.gui.SummaryScreen;
import no.vestlandetmc.shadowtrace.client.handlers.Block;
import no.vestlandetmc.shadowtrace.client.handlers.Colors;
import no.vestlandetmc.shadowtrace.client.handlers.DataManager;
import no.vestlandetmc.shadowtrace.client.network.ReceiveBlockData;
import no.vestlandetmc.shadowtrace.client.renders.DrawBox;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShadowtraceClient implements ClientModInitializer {

	@Getter
	private static final Logger LOGGER = LoggerFactory.getLogger("shadowtrace");
	private KeyBinding keyBinding;
	private ClientWorld lastWorld = null;

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.LAST.register(context -> {
			final MatrixStack matrixStack = context.matrixStack();
			final MinecraftClient client = MinecraftClient.getInstance();
			DrawBox.initialize(matrixStack, client);
		});

		PayloadTypeRegistry.playS2C().register(ReceiveBlockData.ID, ReceiveBlockData.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ReceiveBlockData.ID, (payload, context) -> context.client().execute(() -> {
			DataManager.clearBlocks();

			for (String data : payload.data()) {
				final String[] blockData = data.split(":");
				final String world = getDisplayWorldName(blockData[0]);
				final int locX = Integer.parseInt(blockData[1]);
				final int locY = Integer.parseInt(blockData[2]);
				final int locZ = Integer.parseInt(blockData[3]);
				final String name = blockData[4];
				final long timestamp = Long.parseLong(blockData[5]);
				final Colors color = Colors.fromStringOrDefault(name);
				final BlockPos blockPos = new BlockPos(locX, locY, locZ);
				final Block block;

				if (DataManager.hasBlock(name)) {
					block = DataManager.getBlock(name);
					block.addBlockPos(blockPos, timestamp);
				} else {
					block = new Block(world, name, color);
					block.addBlockPos(blockPos, timestamp);
					DataManager.addBlock(name, block);
				}
			}
		}));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client != null && client.world != null) {
				if (lastWorld != client.world) {
					DataManager.clearBlocks();
					lastWorld = client.world;
				}
			}
		});

		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"shadowtrace.keybind",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"ShadowTrace"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				MinecraftClient.getInstance().setScreen(new SummaryScreen());
			}
		});
	}

	private String getDisplayWorldName(String id) {
		return switch (id) {
			case "world_nether" -> "Nether";
			case "world_the_end" -> "The End";
			case "world" -> "Overworld";
			default -> id;
		};
	}
}
