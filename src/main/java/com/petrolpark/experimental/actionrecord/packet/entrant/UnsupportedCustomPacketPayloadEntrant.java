package com.petrolpark.experimental.actionrecord.packet.entrant;

import com.petrolpark.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public class UnsupportedCustomPacketPayloadEntrant<PAYLOAD extends CustomPacketPayload> implements ICustomPacketPayloadEntrant<PAYLOAD> {

    public final ActionRecordEntryResult entryResult;

    public static final <PAYLOAD extends CustomPacketPayload> UnsupportedCustomPacketPayloadEntrant<?> create(CustomPacketPayload.Type<PAYLOAD> payloadType) {
        return new UnsupportedCustomPacketPayloadEntrant<>(payloadType);
    };

    protected UnsupportedCustomPacketPayloadEntrant(CustomPacketPayload.Type<PAYLOAD> payloadType) {
        entryResult = ActionRecordEntryResult.unsupported(payloadType);
    };

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel level, PAYLOAD packet) {
        return entryResult;
    };
    
};
