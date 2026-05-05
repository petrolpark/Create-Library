package com.petrolpark.compat.jei.ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.JEITextureDrawable;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
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
        @SuppressWarnings("null")
        private final RegistryAccess registryAccess = mc.level.registryAccess();

        public Registry<Biome> getRegistry() {
            return registryAccess.registryOrThrow(Registries.BIOME);
        };

        @Override
        public IIngredientType<Biome> getIngredientType() {
            return TYPE;
        };

        @Override
        public String getDisplayName(@Nonnull Biome ingredient) {
            return getDisplayNameComponent(ingredient).getString();
        };

        public Component getDisplayNameComponent(@Nonnull Biome ingredient) {
            final ResourceLocation rl = getResourceLocation(ingredient);
            if (rl == null) return Component.translatable("biome.petrolpark.unknown");
            return Component.translatable(getResourceLocation(ingredient).toLanguageKey("biome"));
        };

        @Override
        public String getUniqueId(@Nonnull Biome ingredient, @Nonnull UidContext context) {
            final ResourceLocation rl = getResourceLocation(ingredient);
            if (rl == null) return "Unknown Biome";
            return rl.toString();
        };

        @Override
        public ResourceLocation getResourceLocation(@Nonnull Biome ingredient) {
            return registryAccess.registryOrThrow(Registries.BIOME).getKey(ingredient);
        };

        @Override
        public Biome copyIngredient(@Nonnull Biome ingredient) {
            return ingredient;
        };

        @Override
        public String getErrorInfo(@Nonnull Biome ingredient) {
            return "";
        };

        @Override
        public String getDisplayModId(@Nonnull Biome ingredient) {
            final ResourceLocation rl = getResourceLocation(ingredient);
            if (rl == null) return "unknown";
            return rl.getNamespace();
        };

    };

    public static class Renderer implements IIngredientRenderer<Biome> {

        private final JEITextureDrawable globe = JEITextureDrawable.of(PetrolparkGuiTexture.JEI_GLOBE);

        @Override
        public void render(@Nonnull GuiGraphics guiGraphics, @Nonnull Biome ingredient) {
            globe.draw(guiGraphics, 0, 1);
        }

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
