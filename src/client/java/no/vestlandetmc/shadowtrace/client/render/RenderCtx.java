package no.vestlandetmc.shadowtrace.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

public class RenderCtx {

	private static BufferAllocator ALLOC;
	private static VertexConsumerProvider.Immediate VCP;

	public static void init() {
		if (ALLOC == null) {
			ALLOC = new BufferAllocator(2 * 1024 * 1024);
			VCP = VertexConsumerProvider.immediate(ALLOC);
		}
	}

	public static VertexConsumerProvider.Immediate vcp() {
		return VCP;
	}

	public static void endFrame() {
		if (VCP != null) VCP.draw();
		if (ALLOC != null) ALLOC.clear();
	}

	public static void dispose() {
		VCP = null;
		ALLOC = null;
	}
}
