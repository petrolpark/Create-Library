package com.petrolpark.compat.create.core.recipe;

import java.util.Optional;

import com.petrolpark.RequiresCreate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@RequiresCreate
public interface IBiomeSpecificProcessingRecipe {

    public Optional<HolderSet<Biome>> getAllowedBiomes();

    public default boolean isValidIn(Holder<Biome> biome) {
        return getAllowedBiomes().map(set -> set.contains(biome)).orElse(true);
    };

    public default boolean isValidAt(Level level, BlockPos pos) {
        return isValidIn(level.getBiome(pos));
    };

};
