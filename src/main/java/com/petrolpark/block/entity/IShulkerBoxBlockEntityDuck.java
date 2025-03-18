package com.petrolpark.block.entity;

import java.util.stream.Stream;

import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.GenericContamination;

import net.minecraft.core.HolderLookup;

public interface IShulkerBoxBlockEntityDuck {
    
    public GenericContamination getContamination();

    public void contaminateAll(HolderLookup.Provider registries, Stream<Contaminant> contaminants);
};
