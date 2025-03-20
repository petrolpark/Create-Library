package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.tube.BuildTubePacket;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.createmod.catnip.net.base.BasePacketPayload.PacketTypeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum CreatePackets implements PacketTypeProvider {

    BUILD_TUBE(BuildTubePacket.class, BuildTubePacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> CreatePackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Petrolpark.asResource(name().toLowerCase())),
			clazz, codec
		);
	};

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
		return (CustomPacketPayload.Type<T>) type.type();
	};

	public static void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Petrolpark.MOD_ID, 1);
		for (CreatePackets packet : CreatePackets.values()) {
			packetRegistry.registerPacket(packet.type);
		};
		packetRegistry.registerAllPackets();
	};
};
