package com.petrolpark.compat.create.common.redstone.programmer;

import com.petrolpark.compat.create.PetrolparkCreatePackets;
import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public final class RefreshRedstoneProgrammerScreenPacket implements ClientboundPacketPayload, RecordablePacketPayload {

    public static final RefreshRedstoneProgrammerScreenPacket INSTANCE = new RefreshRedstoneProgrammerScreenPacket();
    public static final StreamCodec<ByteBuf, RefreshRedstoneProgrammerScreenPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RefreshRedstoneProgrammerScreenPacket() {};

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkCreatePackets.REFRESH_REDSTONE_PROGRAMMER_SCREEN;
    };

    @Override
    public void handle(LocalPlayer player) {
        if (player.containerMenu instanceof RedstoneProgrammerMenu menu) menu.refreshSlots();
    };

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel serverLevel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEntryResult'");
    };


};
