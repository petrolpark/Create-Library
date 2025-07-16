package com.petrolpark.core.extendedinventory;

import com.petrolpark.PetrolparkPackets;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ExtraInventorySizeChangePacket(int extraInventorySize, int extraHotbarSlots, boolean requestFullState) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, ExtraInventorySizeChangePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ExtraInventorySizeChangePacket::extraInventorySize,
        ByteBufCodecs.INT, ExtraInventorySizeChangePacket::extraHotbarSlots,
        ByteBufCodecs.BOOL, ExtraInventorySizeChangePacket::requestFullState,
        ExtraInventorySizeChangePacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.EXTRA_INVENTORY_SIZE_CHANGE;
    };

    @Override
    public void handle(LocalPlayer player) {
        ExtendedInventoryClientHandler.handleExtendedInventorySizeChange(this);
    };
    
};
