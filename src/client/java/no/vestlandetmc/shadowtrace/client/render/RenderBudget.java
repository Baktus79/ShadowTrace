package no.vestlandetmc.shadowtrace.client.render;

public class RenderBudget {

	private static final int MAX_LINES = 10_000;
	private static int linesThisFrame = 0;

	public static boolean canDraw(int linesToAdd) {
		if (linesThisFrame + linesToAdd > MAX_LINES) return false;
		linesThisFrame += linesToAdd;
		return true;
	}

	public static void reset() {
		linesThisFrame = 0;
	}
}
