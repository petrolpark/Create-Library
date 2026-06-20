package com.petrolpark.experimental.actionrecord.packet.entrant;

import com.petrolpark.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.server.level.ServerLevel;

public interface IPacketEntrant<PACKET> {
    
    public ActionRecordEntryResult getEntryResult(ServerLevel level, PACKET packet);
};
