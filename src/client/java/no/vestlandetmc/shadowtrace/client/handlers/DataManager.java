package no.vestlandetmc.shadowtrace.client.handlers;

import lombok.Getter;

import java.util.HashMap;

public class DataManager {

	@Getter
	private static final HashMap<String, Block> blocks = new HashMap<>();

	public static void addBlock(String name, Block block) {
		blocks.put(name, block);
	}

	public static boolean hasBlock(String name) {
		return blocks.containsKey(name);
	}

	public static Block getBlock(String name) {
		return blocks.get(name);
	}

	public static void clearBlocks() {
		blocks.clear();
	}
}
