package com.petrolpark.core.actionrecord;

import com.mojang.serialization.Codec;

import net.minecraft.server.level.ServerPlayer;

public interface IRecordedAction<ACTION extends IRecordedAction<? super ACTION>> {

    public void play(ServerPlayer player) throws RecordedActionExecutionException;

    public Codec<ACTION> codec();
};
