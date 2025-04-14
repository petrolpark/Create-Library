package com.petrolpark.core.simulation;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

public class SingleBiomeManager extends BiomeManager {

    public final Holder<Biome> biomeHolder;

    public SingleBiomeManager(Holder<Biome> biomeHolder) {
        super((x, y, z) -> biomeHolder, 0l);
        this.biomeHolder = biomeHolder;
    };

    @Override
    public Holder<Biome> getBiome(@Nonnull BlockPos pos) {
        return biomeHolder;
    };

    @Override
    public Holder<Biome> getNoiseBiomeAtPosition(@Nonnull BlockPos pos) {
        return biomeHolder;
    };

    @Override
    public Holder<Biome> getNoiseBiomeAtPosition(double x, double y, double z) {
        return biomeHolder;
    };

    @Override
    public Holder<Biome> getNoiseBiomeAtQuart(int x, int y, int z) {
        return biomeHolder;
    };

};
