package com.petrolpark.compat.create.shared.registry;

import java.util.Locale;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionHarnessMappingPacket;
import com.petrolpark.compat.create.shared.content.redstone.programmer.ChangeRedstoneProgrammerPowerPacket;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RefreshRedstoneProgrammerScreenPacket;
import com.petrolpark.compat.create.shared.content.redstone.programmer.SetRedstoneProgramPacket;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.BasePacketPayload.PacketTypeProvider;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum SharedCreatePackets implements PacketTypeProvider {

	// Client -> server
	SET_REDSTONE_PROGRAM(SetRedstoneProgramPacket.class, SetRedstoneProgramPacket.STREAM_CODEC),

	// Server -> client
	CHANGE_REDSTONE_PROGRAMMER_POWER(ChangeRedstoneProgrammerPowerPacket.class, ChangeRedstoneProgrammerPowerPacket.STREAM_CODEC),
	HORSE_MILL_CONTRAPTION_HARNESS_MAPPING(HorseMillContraptionHarnessMappingPacket.class, HorseMillContraptionHarnessMappingPacket.STREAM_CODEC),
	REFRESH_REDSTONE_PROGRAMMER_SCREEN(RefreshRedstoneProgrammerScreenPacket.class, RefreshRedstoneProgrammerScreenPacket.STREAM_CODEC),
	;

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> SharedCreatePackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Petrolpark.asResource(name().toLowerCase(Locale.ROOT))),
			clazz, codec
		);
	};

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
		return (CustomPacketPayload.Type<T>) type.type();
	};

	public static final void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Petrolpark.MOD_ID, 1);
		for (SharedCreatePackets packet : SharedCreatePackets.values()) {
			packetRegistry.registerPacket(packet.type);
		};
		packetRegistry.registerAllPackets();
	};
};
