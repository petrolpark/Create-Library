package com.petrolpark.network;

import java.util.function.Function;

import com.petrolpark.Petrolpark;
import com.petrolpark.network.packet.C2SPacket;
import com.petrolpark.network.packet.S2CPacket;
import com.petrolpark.team.packet.BindTeamBlockPacket;
import com.petrolpark.team.packet.BindTeamItemPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public class PetrolparkMessages {

    private static SimpleChannel INSTANCE;
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    };

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
            .named(Petrolpark.asResource("messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

        addC2SPacket(BindTeamBlockPacket.class, BindTeamBlockPacket::new);
        addC2SPacket(BindTeamItemPacket.class, BindTeamItemPacket::new);
    };

    public static <T extends S2CPacket> void addS2CPacket(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        INSTANCE.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(decoder)
            .encoder(T::toBytes)
            .consumerMainThread(T::handle)
            .add();
    };

    public static <T extends C2SPacket> void addC2SPacket(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        INSTANCE.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(decoder)
            .encoder(T::toBytes)
            .consumerMainThread(T::handle)
            .add();
    };

    public static void sendToServer(C2SPacket message) {
        INSTANCE.sendToServer(message);
    };

    public static void sendToClient(S2CPacket message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    };

    // public static void sendToAllClientsInDimension(S2CPacket message, ServerLevel level) {
    //     INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    // };

    public static void sendToAllClients(S2CPacket message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    };

    // public static void sendToAllClientsNear(S2CPacket message, BlockSource location) {
    //     INSTANCE.send(PacketDistributor.NEAR.with(TargetPoint.p(location.x(), location.y(), location.z(), 32d, location.getLevel().dimension())), message);
    // };

    // public static void sendToClientsTrackingEntity(S2CPacket message, Entity trackedEntity) {
    //     INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackedEntity), message);
    // };

    // public static void sendToClientsTrackingChunk(S2CPacket message, LevelChunk chunk) {
    //     INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    // };
};
