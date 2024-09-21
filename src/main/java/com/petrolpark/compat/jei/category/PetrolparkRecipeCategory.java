package com.petrolpark.compat.jei.category;

import com.petrolpark.compat.jei.BiomeSpecificTooltipHelper;
import com.petrolpark.compat.jei.ingredient.BiomeIngredientType;
import com.petrolpark.recipe.advancedprocessing.IBiomeSpecificProcessingRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
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
            .addIngredients(BiomeIngredientType.TYPE, BiomeSpecificTooltipHelper.getAllBiomes(recipe).toList())
            .addRichTooltipCallback(BiomeSpecificTooltipHelper.getAllowedBiomeList(recipe)); 
    };
    
};
