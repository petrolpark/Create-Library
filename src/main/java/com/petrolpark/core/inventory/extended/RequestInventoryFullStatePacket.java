package com.petrolpark.core.inventory.extended;

import com.petrolpark.PetrolparkPackets;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class RequestInventoryFullStatePacket implements ServerboundPacketPayload {

    public static final RequestInventoryFullStatePacket INSTANCE = new RequestInventoryFullStatePacket();

    private RequestInventoryFullStatePacket() {};

    public static final StreamCodec<ByteBuf, RequestInventoryFullStatePacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REQUEST_INVENTORY_FULL_STATE;
    };

    @Override
    public void handle(ServerPlayer player) {
        player.inventoryMenu.broadcastFullState();
        player.inventoryMenu.sendAllDataToRemote();
    };
    
};
