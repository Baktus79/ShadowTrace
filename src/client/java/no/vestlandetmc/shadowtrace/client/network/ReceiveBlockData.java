package no.vestlandetmc.shadowtrace.client.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record ReceiveBlockData(List<String> data) implements CustomPayload {

	public static final CustomPayload.Id<ReceiveBlockData> ID = new CustomPayload.Id<>(Identifier.of("shadowtrace", "transfer_block_data"));
	public static final PacketCodec<RegistryByteBuf, ReceiveBlockData> CODEC = PacketCodec.tuple(
			new PacketCodec<ByteBuf, List<String>>() {
				public List<String> decode(ByteBuf byteBuf) {
					final List<String> messageList = new ArrayList<>();
					int numStrings = byteBuf.readInt();

					for (int i = 0; i < numStrings; i++) {
						int strLength = byteBuf.readInt();
						byte[] strBytes = new byte[strLength];
						byteBuf.readBytes(strBytes);
						messageList.add(new String(strBytes, StandardCharsets.UTF_8));
					}

					return messageList;
				}

				public void encode(ByteBuf byteBuf, List<String> messageList) {
					byteBuf.writeInt(messageList.size());
					
					for (String message : messageList) {
						byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
						byteBuf.writeInt(bytes.length);
						byteBuf.writeBytes(bytes);
					}
				}
			},
			ReceiveBlockData::data, ReceiveBlockData::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
