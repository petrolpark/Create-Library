package petrolpark.mc.library.experimental.actionrecord.packet.entrant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PacketEntrants {
    
    protected static final Map<PacketType<? extends Packet<?>>, IVanillaPacketEntrant<? extends PacketListener, ? extends Packet<?>>> PACKET_ENTRANTS = new HashMap<>();
    protected static final Map<CustomPacketPayload.Type<?>, ICustomPacketPayloadEntrant<?>> CUSTOM_PAYLOAD_ENTRANTS = new HashMap<>();

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> getEntrant(PacketType<PACKET> packetType) {
        return PACKET_ENTRANTS.computeIfAbsent(packetType, UnsupportedVanillaPacketEntrant::create).cast();
    };

    @SuppressWarnings("unchecked")
    public static final <PAYLOAD extends CustomPacketPayload> ICustomPacketPayloadEntrant<PAYLOAD> getCustomPayloadEntrant(CustomPacketPayload.Type<PAYLOAD> payloadType) {
        return (ICustomPacketPayloadEntrant<PAYLOAD>)CUSTOM_PAYLOAD_ENTRANTS.computeIfAbsent(payloadType, UnsupportedCustomPacketPayloadEntrant::create);
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> register(PacketType<PACKET> packetType, IVanillaPacketEntrant<LISTENER, PACKET> entrant) {
        return PACKET_ENTRANTS.put(packetType, entrant).cast();
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> registerSimple(PacketType<PACKET> packetType) {
        return registerSimple(packetType, p -> new Object[0]);
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> registerSimple(PacketType<PACKET> packetType, Function<PACKET, Object[]> translationArgs) {
        return register(packetType, new SimpleVanillaPacketEntrant<>(packetType, translationArgs));
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> registerSimple(PacketType<PACKET> packetType, Function<PACKET, Object[]> translationArgs, Function<PACKET, Object[]> advancedTranslationArgs) {
        return register(packetType, new SimpleVanillaPacketEntrant<>(packetType, translationArgs, advancedTranslationArgs));
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> registerSingleTranslationArg(PacketType<PACKET> packetType, Function<PACKET, Object> translationArg) {
        return registerSimple(packetType, translationArg.andThen(obj -> new Object[]{obj}));
    };

    public static final <LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> IVanillaPacketEntrant<LISTENER, PACKET> registerBoolean(PacketType<PACKET> packetType, Predicate<PACKET> predicate) {
        return register(packetType, new BooleanVanillaPacketEntrant<>(packetType, predicate));
    };

    @SuppressWarnings("unchecked")
    public static final <PAYLOAD extends CustomPacketPayload> ICustomPacketPayloadEntrant<PAYLOAD> registerCustomPayload(CustomPacketPayload.Type<PAYLOAD> payloadType, ICustomPacketPayloadEntrant<? super PAYLOAD> entrant) {
        return (ICustomPacketPayloadEntrant<PAYLOAD>)CUSTOM_PAYLOAD_ENTRANTS.put(payloadType, entrant);
    };

};
