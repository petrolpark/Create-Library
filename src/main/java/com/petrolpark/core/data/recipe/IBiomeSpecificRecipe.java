package com.petrolpark.core.data.recipe;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeSpecificRecipe {

    public Optional<HolderSet<Biome>> getAllowedBiomes();

    public static boolean isValidIn(Optional<HolderSet<Biome>> biomes, Holder<Biome> biome) {
        return biomes.map(set -> set.contains(biome)).orElse(true);
    };

    public default boolean isValidIn(Holder<Biome> biome) {
        return isValidIn(getAllowedBiomes(), biome);
    };

    public default boolean isValidAt(Level level, BlockPos pos) {
        return isValidIn(level.getBiome(pos));
    };

};
