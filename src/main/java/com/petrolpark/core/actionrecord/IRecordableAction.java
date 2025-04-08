package com.petrolpark.core.actionrecord;

import com.mojang.serialization.Codec;

import net.minecraft.server.level.ServerPlayer;

public interface IRecordableAction<ACTION extends IRecordableAction<? super ACTION>> {

    public void play(ServerPlayer player);

    public Codec<ACTION> codec();
};
