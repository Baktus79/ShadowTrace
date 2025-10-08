package no.vestlandetmc.shadowtrace.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import no.vestlandetmc.shadowtrace.client.render.RenderHooks;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void shadowtrace$afterEverything(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
		MatrixStack matrices = new MatrixStack();
		matrices.loadIdentity();
		matrices.multiplyPositionMatrix(positionMatrix);

		BufferAllocator bufferAllocator = new BufferAllocator(4 * 1024 * 1024);
		VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(bufferAllocator);

		RenderHooks.afterWorldRender(tickCounter, camera, matrices, vcp);
		vcp.draw();
		bufferAllocator.clear();
	}
}
