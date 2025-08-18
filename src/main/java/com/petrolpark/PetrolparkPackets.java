package com.petrolpark;

import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.entrant.ICustomPacketPayloadEntrant;
import com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;
import com.petrolpark.core.extendedinventory.ExtraInventorySizeChangePacket;
import com.petrolpark.core.extendedinventory.RequestInventoryFullStatePacket;
import com.petrolpark.core.team.packet.BindTeamBlockPacket;
import com.petrolpark.core.team.packet.BindTeamItemPacket;
import com.petrolpark.core.team.scoreboard.ScoreboardTeamComponentChangedPacket;
import com.petrolpark.core.team.singleplayer.SinglePlayerTeamComponentChangedPacket;

import com.petrolpark.shadereffect.packet.InitShaderPacket;
import com.petrolpark.shadereffect.packet.MEIShaderRemovePacket;
import com.petrolpark.shadereffect.packet.RemoveAllShadersPacket;
import com.petrolpark.shadereffect.packet.SyncInitialDurationPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public enum PetrolparkPackets implements BasePacketPayload.PacketTypeProvider, ICustomPacketPayloadEntrant<RecordablePacketPayload> {
    
	// Client -> server
    BIND_TEAM_ITEM(BindTeamItemPacket.class, BindTeamItemPacket.STREAM_CODEC),
    BIND_TEAM_BLOCK(BindTeamBlockPacket.class, BindTeamBlockPacket.STREAM_CODEC),
	REQUEST_INVENTORY_FULL_STATE(RequestInventoryFullStatePacket.class, RequestInventoryFullStatePacket.STREAM_CODEC, false),

	// Server -> client
	SINGLE_PLAYER_TEAM_COMPONENT_CHANGED(SinglePlayerTeamComponentChangedPacket.class, SinglePlayerTeamComponentChangedPacket.STREAM_CODEC, false),
    SCOREBOARD_TEAM_COMPONENT_CHANGED(ScoreboardTeamComponentChangedPacket.class, ScoreboardTeamComponentChangedPacket.STREAM_CODEC, false),
    EXTRA_INVENTORY_SIZE_CHANGE(ExtraInventorySizeChangePacket.class, ExtraInventorySizeChangePacket.STREAM_CODEC, false),
	MEI_SHADER_REMOVE(MEIShaderRemovePacket.class, MEIShaderRemovePacket.STREAM_CODEC, false),
	SYNC_INITIAL_DURATION(SyncInitialDurationPacket.class, SyncInitialDurationPacket.STREAM_CODEC, false),
	REMOVE_ALL_SHADERS(RemoveAllShadersPacket.class, RemoveAllShadersPacket.STREAM_CODEC, false),
	INIT_SHADER(InitShaderPacket.class, InitShaderPacket.STREAM_CODEC, false)
	;

    private final CatnipPacketRegistry.PacketType<?> type;
	private final boolean recordable;

	<T extends BasePacketPayload & RecordablePacketPayload> PetrolparkPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		this(clazz, codec, true);
	};

	<T extends BasePacketPayload> PetrolparkPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, boolean recordable) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Petrolpark.asResource(name().toLowerCase())),
			clazz, codec
		);
		this.recordable = recordable;
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
		for (PetrolparkPackets packet : PetrolparkPackets.values()) {
			packetRegistry.registerPacket(packet.type);
			if (packet.recordable) PacketEntrants.registerCustomPayload(packet.getType(), packet);
		};
		packetRegistry.registerAllPackets();
	};
};
