package com.petrolpark.experimental.actionrecord.packet.entrant;

import com.petrolpark.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public abstract class AlwaysEnterPacketEntrant<PACKET> implements IPacketEntrant<PACKET> {
    
    @Override
    public final ActionRecordEntryResult getEntryResult(ServerLevel level, PACKET packet) {
        return ActionRecordEntryResult.enter(getDescription(packet), getAdvancedDescription(packet));
    };

    public abstract Component getDescription(PACKET packet);

    public Component getAdvancedDescription(PACKET packet) {
        return null;
    };
};
