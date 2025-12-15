package com.petrolpark.compat.jei.ingredient;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateIngredientType implements IIngredientType<BlockState> {

    public static final BlockStateIngredientType TYPE = new BlockStateIngredientType();
    public static final BlockStateIngredientType.Helper HELPER = new BlockStateIngredientType.Helper();
    public static final BlockStateIngredientType.Renderer RENDERER = new BlockStateIngredientType.Renderer();

    @Override
    public Class<? extends BlockState> getIngredientClass() {
        return BlockState.class;
    };

    public static class Helper implements IIngredientHelper<BlockState> {

        @Override
        public IIngredientType<BlockState> getIngredientType() {
            return TYPE;
        };

        @Override
        public String getDisplayName(@Nonnull BlockState ingredient) {
            return ingredient.getBlock().getName().getString();
        };

        @Override
        public String getUniqueId(@Nonnull BlockState ingredient, @Nonnull UidContext context) {
            return ingredient.toString();
        };

        @Override
        @SuppressWarnings("deprecation")
        public ResourceLocation getResourceLocation(@Nonnull BlockState ingredient) {
            return ingredient.getBlock().builtInRegistryHolder().getKey().location();
        };

        @Override
        public BlockState copyIngredient(@Nonnull BlockState ingredient) {
            return ingredient;
        };

        @Override
        public String getErrorInfo(@Nonnull BlockState ingredient) {
            return getUniqueId(ingredient, UidContext.Ingredient);
        };

    };

    public static class Renderer implements IIngredientRenderer<BlockState> {

        private final Minecraft mc = Minecraft.getInstance();

        @Override
        @SuppressWarnings("deprecation")
        public void render(@Nonnull GuiGraphics guiGraphics, @Nonnull BlockState ingredient) {
            mc.getBlockRenderer().renderSingleBlock(ingredient, guiGraphics.pose(), guiGraphics.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY);
        };

        @Override
        public List<Component> getTooltip(@Nonnull BlockState ingredient, @Nonnull TooltipFlag tooltipFlag) {
            return Collections.emptyList();
        };

    };
    
};
