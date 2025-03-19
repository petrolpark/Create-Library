package com.petrolpark;

import com.petrolpark.team.packet.BindTeamBlockPacket;
import com.petrolpark.team.packet.BindTeamItemPacket;
import com.petrolpark.team.scoreboard.ScoreboardTeamComponentChangedPacket;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum PetrolparkPackets implements BasePacketPayload.PacketTypeProvider {
    
    BIND_TEAM_ITEM(BindTeamItemPacket.class, BindTeamItemPacket.STREAM_CODEC),
    BIND_TEAM_BLOCK(BindTeamBlockPacket.class, BindTeamBlockPacket.STREAM_CODEC),
    SCOREBOARD_TEAM_COMPONENT_CHANGED(ScoreboardTeamComponentChangedPacket.class, ScoreboardTeamComponentChangedPacket.STREAM_CODEC),
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> PetrolparkPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
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
		for (PetrolparkPackets packet : PetrolparkPackets.values()) {
			packetRegistry.registerPacket(packet.type);
		};
		packetRegistry.registerAllPackets();
	};
};
