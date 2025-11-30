package com.petrolpark.compat.create.common.redstone.programmer;

import com.petrolpark.compat.create.CreatePackets;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerMenu.DummyRedstoneProgram;
import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public record ChangeRedstoneProgrammerPowerPacket(boolean powered) implements ClientboundPacketPayload, RecordablePacketPayload {

    public static final StreamCodec<ByteBuf, ChangeRedstoneProgrammerPowerPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ChangeRedstoneProgrammerPowerPacket::powered, ChangeRedstoneProgrammerPowerPacket::new);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CreatePackets.CHANGE_REDSTONE_PROGRAMMER_POWER;
    };

    @Override
    public void handle(LocalPlayer player) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof RedstoneProgrammerScreen screen && screen.program instanceof DummyRedstoneProgram program) program.powered = powered();
    };

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel serverLevel) {
        // TODO Auto-generated method stub
        return null;
    };
    
};
