package com.petrolpark.compat.create.shared.content.kinetics.horseMill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.petrolpark.compat.create.shared.registry.SharedCreatePackets;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record HorseMillContraptionHarnessMappingPacket(int entityId, Map<UUID, Integer> mapping) implements ClientboundPacketPayload {
    
    public static final StreamCodec<ByteBuf, HorseMillContraptionHarnessMappingPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, HorseMillContraptionHarnessMappingPacket::entityId,
        ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ByteBufCodecs.INT), p -> new HashMap<>(p.mapping),
        HorseMillContraptionHarnessMappingPacket::new
	);

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handle(LocalPlayer player) {
		final Entity entityByID = player.clientLevel.getEntity(entityId);
		if (!(entityByID instanceof HorseMillContraptionEntity contraptionEntity)) return;
		contraptionEntity.getContraption().setHarnessMapping(new HashMap<>(mapping));
		contraptionEntity.calculateGeneratedSpeedAndStress();
	};

	@Override
	public PacketTypeProvider getTypeProvider() {
		return SharedCreatePackets.HORSE_MILL_CONTRAPTION_HARNESS_MAPPING;
	};
};
