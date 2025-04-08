package com.petrolpark.core.actionrecord.packet.entrant;

import com.petrolpark.core.actionrecord.ActionRecordEntryResult;

import net.minecraft.server.level.ServerLevel;

public interface IPacketEntrant<PACKET> {
    
    public ActionRecordEntryResult getEntryResult(ServerLevel level, PACKET packet);
};
