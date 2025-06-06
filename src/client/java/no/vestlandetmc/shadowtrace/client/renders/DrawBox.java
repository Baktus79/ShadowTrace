package no.vestlandetmc.shadowtrace.client.renders;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import no.vestlandetmc.shadowtrace.client.handlers.Block;
import no.vestlandetmc.shadowtrace.client.handlers.DataManager;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class DrawBox {

	public static void initialize(MatrixStack matrixStack, MinecraftClient client) {
		if (client.world == null || client.player == null || DataManager.getBlocks().isEmpty()) return;

		final Camera camera = client.gameRenderer.getCamera();
		final Vec3d cameraPos = camera.getPos();

		VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		RenderLayer layer = RenderLayer.getLines();
		VertexConsumer consumer = consumers.getBuffer(layer);

		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		for (Map.Entry<String, Block> s : DataManager.getBlocks().entrySet()) {
			final int color = s.getValue().getColor().getHexCode();
			final HashMap<BlockPos, Long> blockPositions = s.getValue().getBlockPositions();

			blockPositions.entrySet().stream().filter(e -> isInsideSquare(e.getKey(), client.player)).forEach(e -> {
				final double x = e.getKey().getX() - cameraPos.x;
				final double y = e.getKey().getY() - cameraPos.y;
				final double z = e.getKey().getZ() - cameraPos.z;
				final Box bb = new Box(x, y, z, x + 1, y + 1, z + 1);

				drawOutlineBox(matrixStack, consumer, bb, color);
			});
		}

		consumers.draw();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
	}

	private static void drawOutlineBox(MatrixStack matrices, VertexConsumer consumer, Box box, int color) {
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		// Bunn (4 linjer)
		drawLine(matrix, consumer, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, color);
		drawLine(matrix, consumer, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, color);
		drawLine(matrix, consumer, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, color);
		drawLine(matrix, consumer, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, color);

		// Topp (4 linjer)
		drawLine(matrix, consumer, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, color);
		drawLine(matrix, consumer, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, color);
		drawLine(matrix, consumer, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, color);
		drawLine(matrix, consumer, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, color);

		// Hjørner (4 linjer)
		drawLine(matrix, consumer, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, color);
		drawLine(matrix, consumer, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, color);
		drawLine(matrix, consumer, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, color);
		drawLine(matrix, consumer, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, color);
	}

	private static void drawLine(Matrix4f matrix, VertexConsumer consumer,
								 double x1, double y1, double z1,
								 double x2, double y2, double z2,
								 int color) {
		consumer.vertex(matrix, (float) x1, (float) y1, (float) z1).color(color).normal(0f, 1f, 0f);
		consumer.vertex(matrix, (float) x2, (float) y2, (float) z2).color(color).normal(0f, 1f, 0f);
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
}