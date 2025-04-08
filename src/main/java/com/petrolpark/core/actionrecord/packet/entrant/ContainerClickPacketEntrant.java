package com.petrolpark.core.actionrecord.packet.entrant;

import com.petrolpark.util.Lang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;

public class ContainerClickPacketEntrant extends AlwaysEnterPacketEntrant<ServerboundContainerClickPacket> implements IVanillaPacketEntrant<ServerGamePacketListener, ServerboundContainerClickPacket> {

    @Override
    public Component getDescription(ServerboundContainerClickPacket packet) {
        return Lang.clickType(packet.getClickType(), packet.getSlotNum());
    };

    @Override
    public Component getAdvancedDescription(ServerboundContainerClickPacket packet) {
        //TODO
        return super.getAdvancedDescription(packet);
    };

};
