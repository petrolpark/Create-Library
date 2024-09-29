package com.petrolpark.recipe.advancedprocessing;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public interface IBiomeSpecificProcessingRecipe {
    
    public void setAllowedBiomes(Set<BiomeValue> biomes);

    public Set<BiomeValue> getAllowedBiomes();

    public default boolean isValidIn(Holder<Biome> biome) {
        return getAllowedBiomes().size() == 0 || getAllowedBiomes().stream().anyMatch(v -> v.matches(biome));
    };

    public default boolean isValidAt(Level level, BlockPos pos) {
        return isValidIn(level.getBiome(pos));
    };

    public static BiomeValue valueFromString(String string) {
        if (string.startsWith("#")) {
            return new TagBiomeValue(TagKey.create(Registries.BIOME, new ResourceLocation(string.substring(1))));
        } else {
            return (new SingleBiomeValue(new ResourceLocation(string)));
        }
    };

    public static interface BiomeValue {

        public boolean matches(Holder<Biome> biome);

        public Collection<Biome> getBiomes(RegistryAccess registryAccess);

        public String serialize();
    };

    public static class SingleBiomeValue implements BiomeValue {

        public final ResourceLocation biomeId;

        public SingleBiomeValue(ResourceLocation biomeId) {
            this.biomeId = biomeId;
        };

        @Override
        public boolean matches(Holder<Biome> biome) {
            return biome.unwrapKey().map(key -> key.location().equals(biomeId)).orElse(false);
        };
        
        @Override
        public Collection<Biome> getBiomes(RegistryAccess registryAccess) {
            return Collections.singleton(registryAccess.registryOrThrow(Registries.BIOME).getOptional(biomeId).orElseThrow(() -> new IllegalStateException("No biome with ID '"+biomeId.toString()+"'.")));
        };

        @Override
        public String serialize() {
            return biomeId.toString();
        };
    };

    public static class TagBiomeValue implements BiomeValue {

        public final TagKey<Biome> tag;

        public TagBiomeValue(TagKey<Biome> tag) {
            this.tag = tag;
        };

        @Override
        public boolean matches(Holder<Biome> biome) {
            return biome.is(tag);
        };

        @Override
        public Collection<Biome> getBiomes(RegistryAccess registryAccess) {
            return registryAccess.registryOrThrow(Registries.BIOME).getTag(tag).map(named -> named.stream().map(Holder::get).toList()).orElse(Collections.emptyList());
        };

        @Override
        public String serialize() {
            return "#"+tag.location();
        };
    };
};
