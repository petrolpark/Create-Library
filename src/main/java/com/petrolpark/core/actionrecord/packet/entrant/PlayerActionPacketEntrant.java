package com.petrolpark.core.actionrecord.packet.entrant;

import com.petrolpark.util.Lang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

public class PlayerActionPacketEntrant extends AlwaysEnterPacketEntrant<ServerboundPlayerActionPacket> implements IVanillaPacketEntrant<ServerGamePacketListener, ServerboundPlayerActionPacket> {

    @Override
    public Component getDescription(ServerboundPlayerActionPacket packet) {
        return Lang.action(packet.getAction(), packet.getPos());
    };

    @Override
    public Component getAdvancedDescription(ServerboundPlayerActionPacket packet) {
        // TODO
        return super.getAdvancedDescription(packet);
    };
    
};
