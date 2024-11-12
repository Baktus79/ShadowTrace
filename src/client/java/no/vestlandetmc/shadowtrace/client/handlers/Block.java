package no.vestlandetmc.shadowtrace.client.handlers;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

@Getter
public class Block {

	private final HashMap<BlockPos, Long> blockPositions = new HashMap<>();
	private final String world;
	private final String name;
	private final Colors color;


	public Block(String world, String name, Colors color) {
		this.world = world;
		this.name = name;
		this.color = color;
	}

	public int getBroken() {
		return blockPositions.size();
	}

	public void addBlockPos(BlockPos blockPos, long timestamp) {
		blockPositions.put(blockPos, timestamp);
	}
}
