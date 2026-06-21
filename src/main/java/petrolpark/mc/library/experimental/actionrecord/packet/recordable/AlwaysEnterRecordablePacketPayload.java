package petrolpark.mc.library.experimental.actionrecord.packet.recordable;

import petrolpark.mc.library.experimental.actionrecord.ActionRecordEntryResult;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public interface AlwaysEnterRecordablePacketPayload extends RecordablePacketPayload {
    
    @Override
    public default ActionRecordEntryResult getEntryResult(ServerLevel level) {
        return ActionRecordEntryResult.enter(getDescription(level), getAdvancedDescription(level));
    };

    public Component getDescription(ServerLevel level);

    public default Component getAdvancedDescription(ServerLevel level) {
        return null;
    };
};
