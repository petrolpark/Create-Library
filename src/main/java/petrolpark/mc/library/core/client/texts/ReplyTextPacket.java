package petrolpark.mc.library.core.client.texts;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import petrolpark.mc.library.registry.PetrolparkPackets;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record ReplyTextPacket(String string) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, ReplyTextPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ReplyTextPacket::string, ReplyTextPacket::new);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PetrolparkPackets.REPLY_TEXT;
    };

    @Override
    public void handle(ServerPlayer player) {
        final WeakReference<Consumer<String>> consumer = ServerTextsManager.awaitingTexts.remove(player);
        if (consumer != null && consumer.get() != null) consumer.get().accept(string());
    };
    
};
