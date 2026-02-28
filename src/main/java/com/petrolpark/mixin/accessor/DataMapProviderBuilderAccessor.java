package com.petrolpark.mixin.accessor;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.datafixers.util.Either;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapEntry;

@Mixin(DataMapProvider.Builder.class)
public interface DataMapProviderBuilderAccessor<T, R> {
    
    @Accessor("values")
    public Map<Either<TagKey<R>, ResourceKey<R>>, Optional<WithConditions<DataMapEntry<T>>>> getValues();
};
