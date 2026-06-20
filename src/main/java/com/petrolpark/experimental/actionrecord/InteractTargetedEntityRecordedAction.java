package com.petrolpark.experimental.actionrecord;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.server.level.ServerPlayer;

@ApiStatus.Experimental
public class InteractTargetedEntityRecordedAction implements IRecordedAction<InteractTargetedEntityRecordedAction> {

    @Override
    public void play(ServerPlayer player) throws RecordedActionExecutionException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'play'");
    };

    @Override
    public Codec<InteractTargetedEntityRecordedAction> codec() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'codec'");
    };
    
};
