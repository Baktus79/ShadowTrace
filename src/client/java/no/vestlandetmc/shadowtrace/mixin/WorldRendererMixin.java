package no.vestlandetmc.shadowtrace.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import no.vestlandetmc.shadowtrace.client.render.RenderBudget;
import no.vestlandetmc.shadowtrace.client.render.RenderCtx;
import no.vestlandetmc.shadowtrace.client.render.RenderHooks;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void shadowtrace$afterEverything(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
		RenderCtx.init();
		RenderBudget.reset();

		MatrixStack matrices = new MatrixStack();
		matrices.loadIdentity();
		matrices.multiplyPositionMatrix(positionMatrix);

		try {
			RenderHooks.afterWorldRender(tickCounter, camera, matrices, RenderCtx.vcp());
		} finally {
			RenderCtx.endFrame();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(true);
		}
	}
}
