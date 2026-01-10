package com.petrolpark.compat.jei.category;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.JEIBlockRenderer;
import com.petrolpark.compat.jei.ingredient.BlockStateIngredientType;
import com.petrolpark.core.recipe.CropFertilizingRecipe;
import com.petrolpark.util.BlockHelper;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CropFertilizingCategory extends PetrolparkRecipeCategory<CropFertilizingRecipe> {

    private static final int BLOCK_SCALE = 23;
    private static final int LEFT_BLOCK_X = 11;
    private static final int RIGHT_BLOCK_X = 79; 
    private static final int BOTTOM_BLOCK_Y = 90;

    private static final BlockState FARMLAND = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE);

    private final JEIBlockRenderer blockRenderer = new JEIBlockRenderer();

    public CropFertilizingCategory(CreateRecipeCategory.Info<CropFertilizingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    protected void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CropFertilizingRecipe recipe, @Nonnull IFocusGroup focuses) {
        final List<BlockState> cropStates = BlockHelper.streamMatching(recipe.crop()).toList();
        
        final IRecipeSlotBuilder cropStateSlot = builder.addSlot(RecipeIngredientRole.INPUT)
            .setPosition(-Integer.MAX_VALUE, -Integer.MAX_VALUE)
            .addIngredients(BlockStateIngredientType.TYPE, cropStates)
            .setSlotName("crops");
            
        final IRecipeSlotBuilder cropStackSlot = builder.addInputSlot(LEFT_BLOCK_X + 7, 3)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStacks(cropStates.stream().map(BlockState::getBlock).<ItemStack>map(ItemStack::new).toList());

        builder.createFocusLink(cropStateSlot, cropStackSlot);

        builder.addInputSlot(52, 40)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.fertilizer());

        addOptionalRequiredBiomeSlot(builder, recipe, 52, 3);

        builder.addOutputSlot(RIGHT_BLOCK_X + 7, 3)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(new ItemStack(recipe.result().getBlock()));

        final List<BlockState> soilStates;
        if (recipe.soil().isPresent()) {
            soilStates = BlockHelper.streamMatching(recipe.soil().get()).toList();
        } else if (recipe.soilResult().isPresent()) {
            soilStates = Collections.singletonList(FARMLAND);
        } else {
            soilStates = Collections.emptyList();
        }

        if (!soilStates.isEmpty()) {
            final boolean requiresSpecificSoil = recipe.soil().isPresent();

            final IRecipeSlotBuilder soilStateSlot = builder.addSlot(requiresSpecificSoil ? RecipeIngredientRole.INPUT : RecipeIngredientRole.RENDER_ONLY)
                .setPosition(-Integer.MAX_VALUE, -Integer.MAX_VALUE)
                .addIngredients(BlockStateIngredientType.TYPE, soilStates)
                .setSlotName("soils");

            final List<BlockState> resultSoilStates = soilStates.stream().map(recipe::getResultSoilState).toList();

            final IRecipeSlotBuilder resultSoilStateSlot = builder.addSlot(RecipeIngredientRole.OUTPUT)
                .setPosition(-Integer.MAX_VALUE, -Integer.MAX_VALUE)
                .addIngredients(BlockStateIngredientType.TYPE, resultSoilStates)
                .setSlotName("result_soils");
            
            final IRecipeSlotBuilder resultSoilStackSlot = builder.addOutputSlot(RIGHT_BLOCK_X - 2, 106)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(resultSoilStates.stream().map(BlockState::getBlock).<ItemStack>map(ItemStack::new).toList());

            if (requiresSpecificSoil) {
                final IRecipeSlotBuilder soilStackSlot = builder.addInputSlot(LEFT_BLOCK_X - 2, 106)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(soilStates.stream().map(BlockState::getBlock).<ItemStack>map(ItemStack::new).toList());
                
                builder.createFocusLink(soilStateSlot, soilStackSlot, resultSoilStateSlot, resultSoilStackSlot);
            } else {
                builder.createFocusLink(soilStateSlot, resultSoilStateSlot, resultSoilStackSlot);
            };
        };

        final List<BlockState> subsoilStates;
        if (recipe.subsoil().isPresent()) {
            subsoilStates = BlockHelper.streamMatching(recipe.subsoil().get()).toList();
        } else if (recipe.subsoilResult().isPresent()) {
            subsoilStates = Collections.singletonList(Blocks.STONE.defaultBlockState());
        } else {
            subsoilStates = Collections.emptyList();
        };

        if (!subsoilStates.isEmpty()) {
            final boolean requiresSpecificSubsoil = recipe.subsoil().isPresent();

            final IRecipeSlotBuilder subsoilStateSlot = builder.addSlot(requiresSpecificSubsoil ? RecipeIngredientRole.INPUT : RecipeIngredientRole.RENDER_ONLY)
                .setPosition(-Integer.MAX_VALUE, -Integer.MAX_VALUE)
                .addIngredients(BlockStateIngredientType.TYPE, subsoilStates)
                .setSlotName("subsoils");

            final List<BlockState> resultSubsoilStates = subsoilStates.stream().map(recipe::getResultSubsoilState).toList();

            final IRecipeSlotBuilder resultSubsoilStateSlot = builder.addSlot(RecipeIngredientRole.OUTPUT)
                .setPosition(-Integer.MAX_VALUE, -Integer.MAX_VALUE)
                .addIngredients(BlockStateIngredientType.TYPE, resultSubsoilStates)
                .setSlotName("result_subsoils");
            
            final IRecipeSlotBuilder resultSubsoilStackSlot = builder.addOutputSlot(RIGHT_BLOCK_X + 16, 106)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(resultSubsoilStates.stream().map(BlockState::getBlock).<ItemStack>map(ItemStack::new).toList());

            if (requiresSpecificSubsoil) {
                final IRecipeSlotBuilder subsoilStackSlot = builder.addInputSlot(LEFT_BLOCK_X + 16, 106)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(subsoilStates.stream().map(BlockState::getBlock).<ItemStack>map(ItemStack::new).toList());
                
                builder.createFocusLink(subsoilStateSlot, subsoilStackSlot, resultSubsoilStateSlot, resultSubsoilStackSlot);
            } else {
                builder.createFocusLink(subsoilStateSlot, resultSubsoilStateSlot, resultSubsoilStackSlot);
            };
        };

    };

    @Override
    protected void draw(@Nonnull CropFertilizingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics graphics, double mouseX, double mouseY) {
        
        final BlockState cropState = recipeSlotsView.findSlotByName("crops").flatMap(slotView -> slotView.getDisplayedIngredient(BlockStateIngredientType.TYPE)).orElseThrow();
        final BlockState resultCropState = recipe.result();
        final BlockState soilState = recipeSlotsView.findSlotByName("soils").flatMap(slotView -> slotView.getDisplayedIngredient(BlockStateIngredientType.TYPE)).orElse(FARMLAND);
        final BlockState resultSoilState = recipeSlotsView.findSlotByName("result_soils").flatMap(slotView -> slotView.getDisplayedIngredient(BlockStateIngredientType.TYPE)).orElse(FARMLAND);
        final BlockState subsoilState = recipeSlotsView.findSlotByName("subsoils").flatMap(slotView -> slotView.getDisplayedIngredient(BlockStateIngredientType.TYPE)).orElse(Blocks.STONE.defaultBlockState());
        final BlockState resultSubsoilState = recipeSlotsView.findSlotByName("result_subsoils").flatMap(slotView -> slotView.getDisplayedIngredient(BlockStateIngredientType.TYPE)).orElse(Blocks.STONE.defaultBlockState());

        AllGuiTextures.JEI_SHADOW.render(graphics, LEFT_BLOCK_X - 11, BOTTOM_BLOCK_Y - 2);
        AllGuiTextures.JEI_SHADOW.render(graphics, RIGHT_BLOCK_X - 11, BOTTOM_BLOCK_Y - 2);

        // Arrow
        PetrolparkGuiTexture.JEI_SHORT_RIGHT_ARROW.render(graphics, 52, 60);

        // Left pillar of Blocks
        graphics.pose().pushPose();
        graphics.pose().translate(LEFT_BLOCK_X, BOTTOM_BLOCK_Y, 200);
        blockRenderer.renderBlock(subsoilState, graphics, BLOCK_SCALE); // Ore or Stone Block
        graphics.pose().translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(soilState, graphics, BLOCK_SCALE); // Farmland Block
        graphics.pose().translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(cropState, graphics, BLOCK_SCALE); // Crop
        graphics.pose().popPose();

        // Right pillar of Blocks
        graphics.pose().pushPose();
        graphics.pose().translate(RIGHT_BLOCK_X, BOTTOM_BLOCK_Y, 200);
        blockRenderer.renderBlock(resultSubsoilState, graphics, BLOCK_SCALE); // Stone Block
        graphics.pose().translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(resultSoilState, graphics, BLOCK_SCALE); // Farmland Block
        graphics.pose().translate(0, 1 - BLOCK_SCALE, BLOCK_SCALE / 2f);
        blockRenderer.renderBlock(resultCropState, graphics, BLOCK_SCALE); // Resultant Crop
        graphics.pose().popPose();
    };
    
};
