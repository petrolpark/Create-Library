package petrolpark.mc.library.registry;

import java.util.Locale;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.effectShaders.packet.InitEffectShaderPacket;
import petrolpark.mc.library.core.client.effectShaders.packet.RemoveAllEffectShadersPacket;
import petrolpark.mc.library.core.client.effectShaders.packet.RemoveEffectShaderPacket;
import petrolpark.mc.library.core.client.effectShaders.packet.SyncMobEffectTotalDurationPacket;
import petrolpark.mc.library.core.client.texts.ReplyTextPacket;
import petrolpark.mc.library.core.client.texts.RequestTextPacket;
import petrolpark.mc.library.core.data.loot.wish.WishGrantedPacket;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtraInventorySizeChangePacket;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.RequestInventoryFullStatePacket;
import petrolpark.mc.library.core.world.entity.player.team.packet.BindTeamBlockPacket;
import petrolpark.mc.library.core.world.entity.player.team.packet.BindTeamItemPacket;
import petrolpark.mc.library.core.world.entity.player.team.scoreboard.ScoreboardTeamComponentChangedPacket;
import petrolpark.mc.library.core.world.entity.player.team.singleplayer.SinglePlayerTeamComponentChangedPacket;
import petrolpark.mc.library.experimental.actionrecord.ActionRecordEntryResult;
import petrolpark.mc.library.experimental.actionrecord.packet.entrant.ICustomPacketPayloadEntrant;
import petrolpark.mc.library.experimental.actionrecord.packet.entrant.PacketEntrants;
import petrolpark.mc.library.experimental.actionrecord.packet.recordable.RecordablePacketPayload;

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
	REPLY_TEXT(ReplyTextPacket.class, ReplyTextPacket.STREAM_CODEC, false),

	// Server -> client
	SINGLE_PLAYER_TEAM_COMPONENT_CHANGED(SinglePlayerTeamComponentChangedPacket.class, SinglePlayerTeamComponentChangedPacket.STREAM_CODEC, false),
    SCOREBOARD_TEAM_COMPONENT_CHANGED(ScoreboardTeamComponentChangedPacket.class, ScoreboardTeamComponentChangedPacket.STREAM_CODEC, false),
    EXTRA_INVENTORY_SIZE_CHANGE(ExtraInventorySizeChangePacket.class, ExtraInventorySizeChangePacket.STREAM_CODEC, false),
	GRANT_WISH(WishGrantedPacket.class, WishGrantedPacket.STREAM_CODEC, false),
	REMOVE_SHADER(RemoveEffectShaderPacket.class, RemoveEffectShaderPacket.STREAM_CODEC, false),
	SYNC_MOB_EFFECT_TOTAL_DURATION(SyncMobEffectTotalDurationPacket.class, SyncMobEffectTotalDurationPacket.STREAM_CODEC, false),
	REMOVE_ALL_EFFECT_SHADERS(RemoveAllEffectShadersPacket.class, RemoveAllEffectShadersPacket.STREAM_CODEC, false),
	INIT_SHADER(InitEffectShaderPacket.class, InitEffectShaderPacket.STREAM_CODEC, false),
	REQUEST_TEXT(RequestTextPacket.class, RequestTextPacket.STREAM_CODEC, false);
	;

    private final CatnipPacketRegistry.PacketType<?> type;
	private final boolean recordable;

	<T extends BasePacketPayload & RecordablePacketPayload> PetrolparkPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		this(clazz, codec, true);
	};

	<T extends BasePacketPayload> PetrolparkPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, boolean recordable) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Petrolpark.asResource(name().toLowerCase(Locale.ROOT))),
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
