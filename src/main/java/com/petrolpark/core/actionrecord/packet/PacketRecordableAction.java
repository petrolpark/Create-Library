package com.petrolpark.core.actionrecord.packet;

import com.mojang.serialization.Codec;
import com.petrolpark.core.actionrecord.IRecordableAction;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;

public record PacketRecordableAction(Packet<ServerGamePacketListener> packet) implements IRecordableAction<PacketRecordableAction> {

    public static final Codec<PacketRecordableAction> CODEC = null; //TODO

    @Override
    public void play(ServerPlayer player) {
        packet.handle(player.connection);
    };

    @Override
    public Codec<PacketRecordableAction> codec() {
        return CODEC;
    };
    
};
