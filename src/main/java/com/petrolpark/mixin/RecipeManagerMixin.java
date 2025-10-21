package com.petrolpark.mixin;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonElement;
import com.petrolpark.Petrolpark;

import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.conditions.WithConditions;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    
    @Inject(
        method = "apply",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void inApply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byTypeBuilder, ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byIdBuilder, RegistryOps<JsonElement> registryOps, Iterator<Map.Entry<ResourceLocation, JsonElement>> iterator, Map.Entry<ResourceLocation, JsonElement> entry, ResourceLocation recipeId, Optional<WithConditions<Recipe<?>>> decoded) {
        if (decoded.isPresent()) Petrolpark.COMPAT_RECIPES.addCompatRecipes(byTypeBuilder, byIdBuilder, registryOps, recipeId, entry.getValue());
    };
};
