package com.petrolpark.core.world.block.entity;

import java.util.stream.Stream;

import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.GenericFlagPole;

import net.minecraft.core.Holder;

public interface IShulkerBoxBlockEntityDuck {
    
    public GenericFlagPole getFlagPole();

    public void flagAll(Stream<Holder<Flag>> flags);
};
