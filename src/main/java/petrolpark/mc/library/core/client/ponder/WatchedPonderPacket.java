package petrolpark.mc.library.core.client.ponder;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkPackets;

public record WatchedPonderPacket(ResourceLocation id) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, WatchedPonderPacket> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(WatchedPonderPacket::new, WatchedPonderPacket::id);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.WATCH_PONDER;
    };

    @Override
    public void handle(ServerPlayer player) {
        PetrolparkCriteriaTriggers.WATCH_PONDER.get().trigger(player, id());
    };
    
};
