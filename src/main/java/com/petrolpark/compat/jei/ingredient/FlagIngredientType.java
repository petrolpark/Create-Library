package com.petrolpark.compat.jei.ingredient;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.core.flags.Flag;
import com.petrolpark.registry.PetrolparkRegistries;
import com.petrolpark.util.Lang;

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
    public static final FlagIngredientType.FullRenderer FULL_RENDERER = new FlagIngredientType.FullRenderer();

    @Override
    public Class<Flag> getIngredientClass() {
        return Flag.class;
    };

    @ParametersAreNonnullByDefault
    public static class EmptyRenderer implements IIngredientRenderer<Flag> {

        @Override
        public void render(GuiGraphics guiGraphics, Flag ingredient) {
            // NOOP
        };

        @Override
        public List<Component> getTooltip(Flag ingredient, TooltipFlag tooltipFlag) {
            return Collections.singletonList(Flag.getNameColored(HELPER.wrapAsHolder(ingredient)));
        };
        
    };

    @ParametersAreNonnullByDefault
    public static class FullRenderer extends EmptyRenderer {

        @Override
        public void render(GuiGraphics guiGraphics, Flag ingredient) {
            final Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Lang.shorten(Flag.getName(HELPER.wrapAsHolder(ingredient)).getString(), font, 162), 1, 1, 0xFFFFFFFF, false);
        };

        @Override
        public int getWidth() {
            return 164;
        };

        @Override
        public int getHeight() {
            return 10;
        };

    };
    
};
