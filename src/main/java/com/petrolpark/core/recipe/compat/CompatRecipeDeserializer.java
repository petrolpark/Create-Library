package com.petrolpark.core.recipe.compat;

import java.util.Optional;

import com.mojang.serialization.Decoder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public interface CompatRecipeDeserializer<R extends Recipe<?>> {

    public ResourceLocation serializerId();
    
    public Decoder<Optional<R>> decoder();

    public ResourceLocation createId(ResourceLocation baseId);
};
