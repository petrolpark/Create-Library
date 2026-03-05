package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.common.redstone.programmer.ChangeRedstoneProgrammerPowerPacket;
import com.petrolpark.compat.create.common.redstone.programmer.RefreshRedstoneProgrammerScreenPacket;
import com.petrolpark.compat.create.common.redstone.programmer.SetRedstoneProgramPacket;
import com.petrolpark.compat.create.core.tube.BuildTubePacket;
import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.entrant.ICustomPacketPayloadEntrant;
import com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.BasePacketPayload.PacketTypeProvider;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public enum PetrolparkCreatePackets implements PacketTypeProvider, ICustomPacketPayloadEntrant<RecordablePacketPayload> {

	// Client -> server
    BUILD_TUBE(BuildTubePacket.class, BuildTubePacket.STREAM_CODEC),
	SET_REDSTONE_PROGRAM(SetRedstoneProgramPacket.class, SetRedstoneProgramPacket.STREAM_CODEC),

	// Server -> client
	CHANGE_REDSTONE_PROGRAMMER_POWER(ChangeRedstoneProgrammerPowerPacket.class, ChangeRedstoneProgrammerPowerPacket.STREAM_CODEC),
	REFRESH_REDSTONE_PROGRAMMER_SCREEN(RefreshRedstoneProgrammerScreenPacket.class, RefreshRedstoneProgrammerScreenPacket.STREAM_CODEC),
	;

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload & RecordablePacketPayload> PetrolparkCreatePackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Petrolpark.asResource(name().toLowerCase())),
			clazz, codec
		);
		//TODO clientbound packets do not need to be recordable
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
		for (PetrolparkCreatePackets packet : PetrolparkCreatePackets.values()) {
			packetRegistry.registerPacket(packet.type);
			PacketEntrants.registerCustomPayload(packet.getType(), packet);
		};
		packetRegistry.registerAllPackets();
	};
};
