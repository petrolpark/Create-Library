package com.petrolpark.experimental.actionrecord.packet.recordable;

import com.petrolpark.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public interface RecordablePacketPayload extends CustomPacketPayload {
    
    public ActionRecordEntryResult getEntryResult(ServerLevel serverLevel);

    public default Component translate(Object... args) {
        return Component.translatable(Util.makeDescriptionId("packet_payload", type().id()), args);
    };
};
