package com.petrolpark.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.recipe.advancedprocessing.IBiomeSpecificProcessingRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BiomeSpecificTooltipHelper {
    
    public static IRecipeSlotTooltipCallback getAllowedBiomeList(IBiomeSpecificProcessingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        RegistryAccess registryAccess = minecraft.level.registryAccess();
        List<ResourceLocation> biomes = new ArrayList<>();
        recipe.getAllowedBiomes().stream().forEach(bv -> biomes.addAll(bv.getBiomes(registryAccess).stream().map(biome -> registryAccess.registryOrThrow(Registries.BIOME).getKey(biome)).toList()));
        return (view, tooltip) -> {
            tooltip.clear();
            tooltip.add(Component.translatable("petrolpark.recipe.biome_specific").withStyle(ChatFormatting.WHITE));
            biomes.forEach(biome -> Component.translatable(biome.toLanguageKey("biome")).withStyle(ChatFormatting.GRAY));
        };
    };
};
