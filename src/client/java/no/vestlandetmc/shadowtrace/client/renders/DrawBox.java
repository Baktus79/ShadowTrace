package no.vestlandetmc.shadowtrace.client.renders;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
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
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		final Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		matrixStack.push();

		for (Map.Entry<String, Block> s : DataManager.getBlocks().entrySet()) {
			final int color = s.getValue().getColor().getHexCode();
			final HashMap<BlockPos, Long> blockPositions = s.getValue().getBlockPositions();

			blockPositions.entrySet().stream().filter(e -> isInsideSquare(e.getKey(), client.player)).forEach(e -> {
				final double x = e.getKey().getX() - cameraPos.x;
				final double y = e.getKey().getY() - cameraPos.y;
				final double z = e.getKey().getZ() - cameraPos.z;
				final Box bb = new Box(x, y, z, x + 1, y + 1, z + 1);
				matrixStack.translate(bb.minX - camera.getPos().x, bb.minY - camera.getPos().y, bb.minZ - camera.getPos().z);
				drawOutlineBox(matrix, bufferBuilder, bb, color);
			});
		}

		try {
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		} catch (Exception ignored) {

		}

		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		matrixStack.pop();
	}

	private static void drawOutlineBox(Matrix4f matrix, BufferBuilder bufferBuilder, Box bb, int color) {
		drawLine(bufferBuilder, matrix, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.minZ, color);
		drawLine(bufferBuilder, matrix, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, color);

		drawLine(bufferBuilder, matrix, bb.maxX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.maxZ, color);
		drawLine(bufferBuilder, matrix, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.minZ, color);

		drawLine(bufferBuilder, matrix, bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, color);
		drawLine(bufferBuilder, matrix, bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color);

		drawLine(bufferBuilder, matrix, bb.maxX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, color);
		drawLine(bufferBuilder, matrix, bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.minZ, color);

		drawLine(bufferBuilder, matrix, bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ, color);
		drawLine(bufferBuilder, matrix, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, color);

		drawLine(bufferBuilder, matrix, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, color);
		drawLine(bufferBuilder, matrix, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, color);
	}

	private static void drawLine(BufferBuilder bufferBuilder, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
		bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z1).color(color);
		bufferBuilder.vertex(matrix, (float) x2, (float) y2, (float) z2).color(color);
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
