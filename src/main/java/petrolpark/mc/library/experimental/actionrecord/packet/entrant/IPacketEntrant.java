package petrolpark.mc.library.experimental.actionrecord.packet.entrant;

import petrolpark.mc.library.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.server.level.ServerLevel;

public interface IPacketEntrant<PACKET> {
    
    public ActionRecordEntryResult getEntryResult(ServerLevel level, PACKET packet);
};
