package com.petrolpark.compat.jei;

import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.recipe.advancedprocessing.IBiomeSpecificProcessingRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeSpecificTooltipHelper {

    public static Stream<Biome> getAllBiomes(IBiomeSpecificProcessingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        RegistryAccess registryAccess = minecraft.level.registryAccess();
        return recipe.getAllowedBiomes().stream().flatMap(bv -> bv.getBiomes(registryAccess).stream());
    };
    
    public static IRecipeSlotRichTooltipCallback getAllowedBiomeList(IBiomeSpecificProcessingRecipe recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        RegistryAccess registryAccess = minecraft.level.registryAccess();
        List<ResourceLocation> biomes = getAllBiomes(recipe).map(biome -> registryAccess.registryOrThrow(Registries.BIOME).getKey(biome)).toList();
        return (view, tooltip) -> {
            tooltip.add(Component.translatable("petrolpark.recipe.biome_specific").withStyle(ChatFormatting.WHITE));
            biomes.forEach(biome -> tooltip.add(Component.translatable(biome.toLanguageKey("biome")).withStyle(ChatFormatting.GRAY)));
        };
    };
};
