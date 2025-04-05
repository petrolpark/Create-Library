package com.petrolpark.core.block.entity;

import java.util.stream.Stream;

import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.GenericContamination;

import net.minecraft.core.Holder;

public interface IShulkerBoxBlockEntityDuck {
    
    public GenericContamination getContamination();

    public void contaminateAll(Stream<Holder<Contaminant>> contaminants);
};
