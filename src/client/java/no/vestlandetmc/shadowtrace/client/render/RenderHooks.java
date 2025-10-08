package no.vestlandetmc.shadowtrace.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import no.vestlandetmc.shadowtrace.client.renders.DrawBox;

public class RenderHooks {

	private RenderHooks() {
	}

	public static void afterWorldRender(RenderTickCounter tickCounter, Camera camera, MatrixStack matrices, VertexConsumerProvider.Immediate vcp) {
		MinecraftClient client = MinecraftClient.getInstance();
		DrawBox.initialize(matrices, client, vcp);
	}
}
