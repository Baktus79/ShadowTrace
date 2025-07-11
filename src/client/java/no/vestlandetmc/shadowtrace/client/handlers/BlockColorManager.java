package no.vestlandetmc.shadowtrace.client.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BlockColorManager {

	private static final File configFile = new File("config/shadowtrace/block_colors.json");
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final Map<String, Integer> blockColors = new HashMap<>();
	private static final int DEFAULT_COLOR = 0xffff00ff;
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockColorManager.class);

	public static void load() {
		if (!configFile.exists()) {
			createDefaultConfig();
		}

		try (FileReader reader = new FileReader(configFile)) {
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> raw = gson.fromJson(reader, type);
			blockColors.clear();
			for (Map.Entry<String, String> entry : raw.entrySet()) {
				String name = entry.getKey().toLowerCase();
				String hex = entry.getValue().replace("#", "");
				try {
					int color = (int) Long.parseLong(hex, 16) | 0xFF000000;
					blockColors.put(name, color);
				} catch (NumberFormatException e) {
					System.err.println("Invalid color for " + name + ": " + hex);
				}
			}
			LOGGER.info("Loaded {} block colors", blockColors.size());
		} catch (Exception e) {
			LOGGER.error("Failed to load block_colors.json: {}", e.getMessage());
		}
	}

	private static void createDefaultConfig() {
		try {
			File folder = configFile.getParentFile();
			if (!folder.exists()) folder.mkdirs();

			Map<String, String> defaults = new HashMap<>();
			defaults.put("coal_ore", "#000000");
			defaults.put("copper_ore", "#bc633d");
			defaults.put("diamond_ore", "#00f2ff");
			defaults.put("gold_ore", "#ffaa00");
			defaults.put("lapis_ore", "#002bff");
			defaults.put("emerald_ore", "#00cd12");
			defaults.put("iron_ore", "#e9c0a4");
			defaults.put("redstone_ore", "#ff0000");
			defaults.put("deepslate_coal_ore", "#000000");
			defaults.put("deepslate_copper_ore", "#bc633d");
			defaults.put("deepslate_diamond_ore", "#00f2ff");
			defaults.put("deepslate_gold_ore", "#ffaa00");
			defaults.put("deepslate_lapis_ore", "#002bff");
			defaults.put("deepslate_emerald_ore", "#00cd12");
			defaults.put("deepslate_iron_ore", "#e9c0a4");
			defaults.put("deepslate_redstone_ore", "#ff0000");
			defaults.put("ancient_debris", "#851b00");
			defaults.put("nether_gold_ore", "#ffaa00");
			defaults.put("spawner", "#9900ff");

			String json = gson.toJson(defaults);
			Files.writeString(configFile.toPath(), json);
			LOGGER.info("Created default block_colors.json");
		} catch (IOException e) {
			LOGGER.error("Failed to create block_colors.json: {}", e.getMessage());
		}
	}

	public static int getColor(String blockName) {
		if (blockName == null) return DEFAULT_COLOR;
		return blockColors.getOrDefault(blockName.toLowerCase(), DEFAULT_COLOR);
	}
}
