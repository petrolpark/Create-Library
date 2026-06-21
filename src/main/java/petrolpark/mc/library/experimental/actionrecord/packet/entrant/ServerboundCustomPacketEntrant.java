package petrolpark.mc.library.experimental.actionrecord.packet.entrant;

import petrolpark.mc.library.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

public final class ServerboundCustomPacketEntrant implements IVanillaPacketEntrant<ServerCommonPacketListener, ServerboundCustomPayloadPacket> {

    @Override
    public ActionRecordEntryResult getEntryResult(ServerLevel level, ServerboundCustomPayloadPacket packet) {
        return getEntryResultForPayload(level, packet.payload().type(), packet.payload());
    };

    @SuppressWarnings("unchecked")
    private <PAYLOAD extends CustomPacketPayload> ActionRecordEntryResult getEntryResultForPayload(ServerLevel level, CustomPacketPayload.Type<PAYLOAD> type, CustomPacketPayload payload) {
        return PacketEntrants.getCustomPayloadEntrant(type).getEntryResult(level, (PAYLOAD)payload);
    };
    
};
