package com.petrolpark.compat.jei.ingredient;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.JEITextureDrawable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.biome.Biome;

public class BiomeIngredientType implements IIngredientType<Biome> {

    public static final BiomeIngredientType TYPE = new BiomeIngredientType();
    public static final Helper HELPER = new Helper();
    public static final Renderer RENDERER = new Renderer();

    @Override
    public Class<? extends Biome> getIngredientClass() {
        return Biome.class;
    };

    public static class Helper implements IIngredientHelper<Biome> {

        private final Minecraft mc = Minecraft.getInstance();
        private final RegistryAccess registryAccess = mc.level.registryAccess();

        @Override
        public IIngredientType<Biome> getIngredientType() {
            return TYPE;
        };

        @Override
        public String getDisplayName(Biome ingredient) {
            return getDisplayNameComponent(ingredient).getString();
        };

        public Component getDisplayNameComponent(Biome ingredient) {
            return Component.translatable(getResourceLocation(ingredient).toLanguageKey("biome"));
        };

        @Override
        public String getUniqueId(Biome ingredient, UidContext context) {
            return getResourceLocation(ingredient).toString();
        };

        @Override
        public ResourceLocation getResourceLocation(Biome ingredient) {
            return registryAccess.registryOrThrow(Registries.BIOME).getKey(ingredient);
        };

        @Override
        public Biome copyIngredient(Biome ingredient) {
            return ingredient;
        };

        @Override
        public String getErrorInfo(@Nullable Biome ingredient) {
            return "";
        };

    };

    public static class Renderer implements IIngredientRenderer<Biome> {

        private final JEITextureDrawable globe = JEITextureDrawable.of(PetrolparkGuiTexture.JEI_GLOBE);

        @Override
        public void render(GuiGraphics guiGraphics, Biome ingredient) {
            globe.draw(guiGraphics);
        };

        @Override
        public List<Component> getTooltip(Biome ingredient, TooltipFlag tooltipFlag) {
            List<Component> tooltip = new ArrayList<>(tooltipFlag.isAdvanced() ? 2 : 1);
            tooltip.add(HELPER.getDisplayNameComponent(ingredient));
            if (tooltipFlag.isAdvanced()) tooltip.add(Component.literal(HELPER.getResourceLocation(ingredient).toString()).withStyle(ChatFormatting.DARK_GRAY));
            return tooltip;
        };

    };
    
};
