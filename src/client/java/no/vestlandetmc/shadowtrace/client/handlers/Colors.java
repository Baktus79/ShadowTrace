package no.vestlandetmc.shadowtrace.client.handlers;

public enum Colors {
	COAL_ORE(0xff000000),
	COPPER_ORE(0xffbc633d),
	DIAMOND_ORE(0xff00f2ff),
	GOLD_ORE(0xffffaa00),
	LAPIS_ORE(0xff002bff),
	EMERALD_ORE(0xff00cd12),
	IRON_ORE(0xffe9c0a4),
	REDSTONE_ORE(0xffff0000),
	DEEPSLATE_COAL_ORE(0xff000000),
	DEEPSLATE_COPPER_ORE(0xffbc633d),
	DEEPSLATE_DIAMOND_ORE(0xff00f2ff),
	DEEPSLATE_GOLD_ORE(0xffffaa00),
	DEEPSLATE_LAPIS_ORE(0xff002bff),
	DEEPSLATE_EMERALD_ORE(0xff00cd12),
	DEEPSLATE_IRON_ORE(0xffe9c0a4),
	DEEPSLATE_REDSTONE_ORE(0xffff0000),
	ANCIENT_DEBRIS(0xff851b00),
	NETHER_GOLD_ORE(0xffffaa00);

	private final int hex;

	Colors(int hex) {
		this.hex = hex;
	}

	public int getHexCode() {
		return hex;
	}
}
