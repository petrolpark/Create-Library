package com.petrolpark.core.actionrecord.packet.entrant;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public interface IVanillaPacketEntrant<LISTENER extends PacketListener, PACKET extends Packet<? super LISTENER>> extends IPacketEntrant<PACKET> {
  
    @SuppressWarnings("unchecked")
    public default <CHILD_LISTENER extends PacketListener, CHILD_PACKET extends Packet<? super CHILD_LISTENER>> IVanillaPacketEntrant<CHILD_LISTENER, CHILD_PACKET> cast() {
        return (IVanillaPacketEntrant<CHILD_LISTENER, CHILD_PACKET>)this;
    };
};
