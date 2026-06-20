package com.petrolpark.experimental.actionrecord.packet;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.petrolpark.experimental.actionrecord.IRecordedAction;
import com.petrolpark.experimental.actionrecord.RecordedActionExecutionException;
import com.petrolpark.registry.PetrolparkRegistries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.connection.ConnectionType;

public record PacketRecordedAction(Packet<? super ServerGamePacketListener> packet) implements IRecordedAction<PacketRecordedAction> {

    @SuppressWarnings("deprecation")
    protected static final Supplier<ProtocolInfo<ServerGamePacketListener>> PROTOCOL_INFO = Suppliers.memoize(() -> 
        GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(
            PetrolparkRegistries.registryAccess(),
            ConnectionType.NEOFORGE
        ))
    );

    public static final Codec<PacketRecordedAction> CODEC = Codec.lazyInitialized(() ->
        Codec.BYTE_BUFFER.<Packet<? super ServerGamePacketListener>>flatXmap(
            byteBuffer -> {
                Packet<? super ServerGamePacketListener> packet;
                try {
                    packet = PROTOCOL_INFO.get().codec().decode(Unpooled.wrappedBuffer(byteBuffer));
                } catch (Throwable e) {
                    return DataResult.error(e::getMessage);
                };
                return DataResult.success(packet);
            },
            packet -> {
                ByteBuffer nioBuffer;
                try {
                    ByteBuf buf = Unpooled.buffer(32768);
                    PROTOCOL_INFO.get().codec().encode(buf, packet);
                    nioBuffer = buf.nioBuffer();
                } catch (Throwable e) {
                    return DataResult.error(e::getMessage);
                };
                return DataResult.success(nioBuffer);
            }
        ).xmap(
            PacketRecordedAction::new,
            PacketRecordedAction::packet
        )
    );

    @Override
    public void play(ServerPlayer player) throws RecordedActionExecutionException {
        packet.handle(player.connection);
    };

    @Override
    public Codec<PacketRecordedAction> codec() {
        return CODEC;
    };
    
};
