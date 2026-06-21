package petrolpark.mc.library.experimental.actionrecord.packet.entrant;

import java.util.function.Predicate;

import net.minecraft.Util;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class BooleanVanillaPacketEntrant<LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> extends BooleanPacketEntrant<PACKET> implements IVanillaPacketEntrant<LISTENER, PACKET> {

    public BooleanVanillaPacketEntrant(PacketType<PACKET> packetType, Predicate<PACKET> predicate) {
        super(Util.makeDescriptionId("packet", packetType.id()), predicate);
    };
    
};
