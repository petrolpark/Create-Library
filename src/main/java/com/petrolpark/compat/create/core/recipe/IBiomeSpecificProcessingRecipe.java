package com.petrolpark.compat.create.core.recipe;

import com.petrolpark.RequiresCreate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@RequiresCreate
public interface IBiomeSpecificProcessingRecipe {
    
    void setAllowedBiomes(HolderSet<Biome> biomes);

    public HolderSet<Biome> getAllowedBiomes();

    public default boolean isValidIn(Holder<Biome> biome) {
        return getAllowedBiomes().contains(biome);
    };

    public default boolean isValidAt(Level level, BlockPos pos) {
        return isValidIn(level.getBiome(pos));
    };

};
