package petrolpark.mc.library.compat.create.shared.content.redstone.programmer;

import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerMenu.DummyRedstoneProgram;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreatePackets;
import petrolpark.mc.library.experimental.actionrecord.ActionRecordEntryResult;
import petrolpark.mc.library.experimental.actionrecord.packet.recordable.RecordablePacketPayload;

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
        return SharedCreatePackets.CHANGE_REDSTONE_PROGRAMMER_POWER;
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
