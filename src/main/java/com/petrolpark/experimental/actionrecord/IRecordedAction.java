package com.petrolpark.experimental.actionrecord;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.server.level.ServerPlayer;

@ApiStatus.Experimental
public interface IRecordedAction<ACTION extends IRecordedAction<? super ACTION>> {

    public void play(ServerPlayer player) throws RecordedActionExecutionException;

    public Codec<ACTION> codec();
};
