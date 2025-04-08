package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.tube.BuildTubePacket;
import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.entrant.ICustomPacketPayloadEntrant;
import com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.createmod.catnip.net.base.BasePacketPayload.PacketTypeProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public enum CreatePackets implements PacketTypeProvider, ICustomPacketPayloadEntrant<RecordablePacketPayload> {

    BUILD_TUBE(BuildTubePacket.class, BuildTubePacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload & RecordablePacketPayload> CreatePackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
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

	@Override
	public ActionRecordEntryResult getEntryResult(ServerLevel level, RecordablePacketPayload packet) {
		return packet.getEntryResult(level);
	};

	public static void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Petrolpark.MOD_ID, 1);
		for (CreatePackets packet : CreatePackets.values()) {
			packetRegistry.registerPacket(packet.type);
			PacketEntrants.registerCustomPayload(packet.getType(), packet);
		};
		packetRegistry.registerAllPackets();
	};
};
