package com.petrolpark.core.actionrecord.packet.entrant;

import com.petrolpark.core.actionrecord.ActionRecordEntryResult;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerLevel;

public final class UnsupportedVanillaPacketEntrant<LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> implements IVanillaPacketEntrant<LISTENER, PACKET> {

    public final ActionRecordEntryResult entryResult;

    public static final UnsupportedVanillaPacketEntrant<?, ?> create(PacketType<?> packetType) {
        return new UnsupportedVanillaPacketEntrant<>(packetType);
    };

    protected UnsupportedVanillaPacketEntrant(PacketType<?> packetType) {
        this.entryResult = ActionRecordEntryResult.unsupported(packetType);
    };

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel level, PACKET packet) {
        //return entryResult;
        return ActionRecordEntryResult.SILENT_IGNORE;
    };
    
};
