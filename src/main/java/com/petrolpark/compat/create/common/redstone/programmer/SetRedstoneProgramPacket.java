package com.petrolpark.compat.create.common.redstone.programmer;

import com.petrolpark.compat.create.CreatePackets;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerMenu.DummyRedstoneProgram;
import com.petrolpark.core.actionrecord.ActionRecordEntryResult;
import com.petrolpark.core.actionrecord.packet.recordable.RecordablePacketPayload;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record SetRedstoneProgramPacket(RedstoneProgram program) implements ServerboundPacketPayload, RecordablePacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRedstoneProgramPacket> STREAM_CODEC = StreamCodec.composite(RedstoneProgram.streamCodec(DummyRedstoneProgram::new), SetRedstoneProgramPacket::program, SetRedstoneProgramPacket::new);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CreatePackets.SET_REDSTONE_PROGRAM;
    };

    @Override
    public void handle(ServerPlayer player) {
        final AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof RedstoneProgrammerMenu programMenu) {
            programMenu.refreshSlots();
            final RedstoneProgram program = programMenu.contentHolder;
            program.unload();
            program.copyFrom(program());
            program.load();
            program.whenChanged();
            CatnipServices.NETWORK.sendToClient(player, RefreshRedstoneProgrammerScreenPacket.INSTANCE);
        };
    }

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel serverLevel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEntryResult'");
    };
    
};
