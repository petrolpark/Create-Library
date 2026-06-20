package com.petrolpark.compat.create.shared.content.redstone.programmer;

import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerMenu.DummyRedstoneProgram;
import com.petrolpark.compat.create.shared.registry.SharedCreatePackets;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.experimental.actionrecord.ActionRecordEntryResult;
import com.petrolpark.experimental.actionrecord.packet.recordable.RecordablePacketPayload;

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
        return SharedCreatePackets.SET_REDSTONE_PROGRAM;
    };

    @Override
    public void handle(ServerPlayer player) {
        final AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof RedstoneProgrammerMenu programMenu) {
            final RedstoneProgram program = programMenu.contentHolder;
            program.unload();
            program.copyFrom(program());
            program.load();
            program.whenChanged();
            CatnipServices.NETWORK.sendToClient(player, RefreshRedstoneProgrammerScreenPacket.INSTANCE);
            if (Math.min(program.getChannels().size() + 1, PetrolparkConfigs.server().redstoneProgrammerMaxChannels.get()) * 2 > programMenu.ghostInventory.getSlots()) // If a channel has been added/removed
                programMenu.refreshSlots();
        };
    }

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel serverLevel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEntryResult'");
    };
    
};
