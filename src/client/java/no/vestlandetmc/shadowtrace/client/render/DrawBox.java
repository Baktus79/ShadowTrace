package no.vestlandetmc.shadowtrace.client.render;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.util.math.BlockPos;
import no.vestlandetmc.shadowtrace.client.handlers.Block;
import no.vestlandetmc.shadowtrace.client.handlers.DataManager;

import java.util.Map;

public class DrawBox {

	public static void initialize(MinecraftClient client, CameraRenderState cameraRenderState) {
		if (client.world == null || client.player == null || DataManager.getBlocks().isEmpty()) return;

		for (Map.Entry<String, Block> s : DataManager.getBlocks().entrySet()) {
			int color = s.getValue().getColor();
			final Color4f malilibColor = convertToColor4f(color | 0xFF000000);

			s.getValue().getBlockPositions().entrySet().stream()
					.filter(e -> isInsideSquare(e.getKey(), client.player))
					.forEach(e -> {
						RenderUtils.renderBlockOutline(e.getKey(), 0.0F, 1.0F, malilibColor, true);
					});
		}
	}

	private static boolean isInsideSquare(BlockPos blockPos, ClientPlayerEntity player) {
		final double playerX = player.getX();
		final double playerY = player.getY();
		final double playerZ = player.getZ();

		final double dx = blockPos.getX() - playerX;
		final double dy = blockPos.getY() - playerY;
		final double dz = blockPos.getZ() - playerZ;
		final double distanceSquared = dx * dx + dy * dy + dz * dz;

		return distanceSquared <= 100 * 100;
	}

	private static Color4f convertToColor4f(int color) {
		float alpha = ((color >> 24) & 0xFF) / 255.0F;
		float red = ((color >> 16) & 0xFF) / 255.0F;
		float green = ((color >> 8) & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		return new Color4f(red, green, blue, alpha);
	}
}