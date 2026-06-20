package com.petrolpark.compat.jei.ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.compat.jei.JEITextureDrawable;
import com.petrolpark.core.client.rendering.PetrolparkGuiTexture;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.biome.Biome;

public class BiomeIngredientType implements IIngredientType<Biome> {

    public static final BiomeIngredientType TYPE = new BiomeIngredientType();
    public static final RegistryIngredientHelper<Biome> HELPER = new RegistryIngredientHelper<>(TYPE, Registries.BIOME, "biome");
    public static final Renderer RENDERER = new BiomeIngredientType.Renderer();

    @Override
    public Class<Biome> getIngredientClass() {
        return Biome.class;
    };

    public static class Renderer implements IIngredientRenderer<Biome> {

        private final JEITextureDrawable globe = JEITextureDrawable.of(PetrolparkGuiTexture.JEI_GLOBE);

        @Override
        public void render(@Nonnull GuiGraphics guiGraphics, @Nonnull Biome ingredient) {
            globe.draw(guiGraphics, 0, 1);
        };

        @Override
        public List<Component> getTooltip(@Nonnull Biome ingredient, @Nonnull TooltipFlag tooltipFlag) {
            ResourceLocation rl = HELPER.getResourceLocation(ingredient);
            if (rl == null) return Collections.emptyList();
            List<Component> tooltip = new ArrayList<>(tooltipFlag.isAdvanced() ? 2 : 1);
            tooltip.add(HELPER.getDisplayNameComponent(ingredient));
            if (tooltipFlag.isAdvanced()) tooltip.add(Component.literal(rl.toString()).withStyle(ChatFormatting.DARK_GRAY));
            return tooltip;
        };

    };
    
};
