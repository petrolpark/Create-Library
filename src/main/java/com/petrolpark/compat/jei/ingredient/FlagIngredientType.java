package com.petrolpark.compat.jei.ingredient;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.core.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.registry.PetrolparkRegistries;
import com.petrolpark.util.Lang;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public class FlagIngredientType implements IIngredientType<Flag> {

    public static final FlagIngredientType TYPE = new FlagIngredientType();
    public static final RegistryIngredientHelper<Flag> HELPER = new RegistryIngredientHelper<>(TYPE, PetrolparkRegistries.Keys.FLAG);
    public static final FlagIngredientType.EmptyRenderer EMPTY_RENDERER = new FlagIngredientType.EmptyRenderer();
    public static final FlagIngredientType.IconRenderer ICON_RENDERER = new FlagIngredientType.IconRenderer();
    public static final FlagIngredientType.FullRenderer FULL_RENDERER = new FlagIngredientType.FullRenderer();

    public static final IDrawable BACKGROUND = new IDrawable() {

        @Override
        public int getWidth() {
            return 152;
        };

        @Override
        public int getHeight() {
            return 11;
        };

        @Override
        public void draw(@Nonnull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.fill(xOffset, yOffset, xOffset + 152, yOffset + 11, 0xFF8B8B8B);
        };
        
    };

    @Override
    public Class<Flag> getIngredientClass() {
        return Flag.class;
    };

    @ParametersAreNonnullByDefault
    public static class EmptyRenderer implements IIngredientRenderer<Flag> {

        @Override
        public void render(GuiGraphics guiGraphics, Flag ingredient) {
            
        };

        @Override
        public List<Component> getTooltip(Flag ingredient, TooltipFlag tooltipFlag) {
            return Collections.singletonList(Flag.getNameColored(HELPER.wrapAsHolder(ingredient)));
        };

    };

    @ParametersAreNonnullByDefault
    public static class IconRenderer extends EmptyRenderer {

        @Override
        public void render(GuiGraphics guiGraphics, Flag ingredient) {
            render(guiGraphics, 0xFF000000 | ingredient.getColor());
        };

        public void render(GuiGraphics guiGraphics, int color) {
            PetrolparkGuiTexture.JEI_FLAGPOLE.render(guiGraphics, 0, 0);
            PetrolparkGuiTexture.JEI_FLAG.render(guiGraphics, 0, 0, color);
        };
        
    };

    @ParametersAreNonnullByDefault
    public static class FullRenderer extends EmptyRenderer {

        @Override
        public void render(GuiGraphics guiGraphics, Flag ingredient) {
            final Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Lang.shorten(Flag.getName(HELPER.wrapAsHolder(ingredient)).getString(), font, 148), 1, 0, ingredient.getColor());
        };

        @Override
        public int getWidth() {
            return 150;
        };

        @Override
        public int getHeight() {
            return 9;
        };

    };

    public static class Icon implements IDrawable {

        public final int color;

        public Icon(int color) {
            this.color = color;
        };

        @Override
        public int getWidth() {
            return 16;
        };

        @Override
        public int getHeight() {
            return 16;
        };

        @Override
        public void draw(@Nonnull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xOffset, yOffset, 0f);
            ICON_RENDERER.render(guiGraphics, color);
            guiGraphics.pose().popPose();
        };

    };
    
};
