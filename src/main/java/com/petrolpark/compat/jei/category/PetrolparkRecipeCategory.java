package com.petrolpark.compat.jei.category;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.BiomeSpecificTooltipHelper;
import com.petrolpark.compat.jei.JEITextureDrawable;
import com.petrolpark.recipe.advancedprocessing.IBiomeSpecificProcessingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public abstract class PetrolparkRecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

    protected final IJeiHelpers helpers;

    public PetrolparkRecipeCategory(Info<T> info, IJeiHelpers helpers) {
        super(info);
        this.helpers = helpers;
    };

    public interface Factory<T extends Recipe<?>> {
		CreateRecipeCategory<T> create(Info<T> info, IJeiHelpers helpers);
	};

    public static void addOptionalRequiredBiomeSlot(IRecipeLayoutBuilder builder, IBiomeSpecificProcessingRecipe recipe, int x, int y) {
        if (!recipe.getAllowedBiomes().isEmpty())builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y)
            .setBackground(getRenderedSlot(), -1, -1)
            .setOverlay(JEITextureDrawable.of(PetrolparkGuiTexture.JEI_GLOBE), 0, 1)
            .addItemStack(new ItemStack(Items.IRON_NUGGET)) // Dummy item so we actually get something generated
            .addTooltipCallback(BiomeSpecificTooltipHelper.getAllowedBiomeList(recipe)); 
    };
    
};
