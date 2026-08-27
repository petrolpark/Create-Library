package petrolpark.mc.library.compat.create.registry;

import java.util.Locale;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.BasePacketPayload.PacketTypeProvider;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.block.tube.BuildTubePacket;
import petrolpark.mc.library.compat.create.core.world.item.valueSettings.ItemValueSettingsPacket;

public enum PetrolparkCreatePackets implements PacketTypeProvider {

	// Client -> server
    BUILD_TUBE(BuildTubePacket.class, BuildTubePacket.STREAM_CODEC),
	ITEM_VALUE_SETTINGS(ItemValueSettingsPacket.class, ItemValueSettingsPacket.STREAM_CODEC),

	// Server -> client
	;

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> PetrolparkCreatePackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
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
		for (PetrolparkCreatePackets packet : PetrolparkCreatePackets.values()) {
			packetRegistry.registerPacket(packet.type);
		};
		packetRegistry.registerAllPackets();
	};
};
