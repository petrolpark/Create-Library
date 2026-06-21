package com.petrolpark.compat.jei.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.StringUtils;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.IntrinsicFlagsCategory.IntrinsicFlagsRecipe;
import com.petrolpark.compat.jei.ingredient.FlagIngredientType;
import com.petrolpark.compat.jei.ingredient.FlagIngredientType.FlagHolderHolder;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.Flaggable;
import com.petrolpark.util.Lang;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

@ParametersAreNonnullByDefault
public class IntrinsicFlagsCategory<INGREDIENT, STACK> extends AbstractRecipeCategory<IntrinsicFlagsRecipe<INGREDIENT>> {

    @SuppressWarnings("unchecked") private static final Class<IntrinsicFlagsRecipe<Item>> ITEM_INTRINSIC_FLAGS_RECIPE_CLASS = (Class<IntrinsicFlagsRecipe<Item>>)(Class<?>)IntrinsicFlagsRecipe.class;
    public static final RecipeType<IntrinsicFlagsRecipe<Item>> ITEM_RECIPE_TYPE = RecipeType.create(Petrolpark.MOD_ID, "item_intrinsic_flags", ITEM_INTRINSIC_FLAGS_RECIPE_CLASS);
    @SuppressWarnings("unchecked") private static final Class<IntrinsicFlagsRecipe<Fluid>> FLUID_INTRINSIC_FLAGS_RECIPE_CLASS = (Class<IntrinsicFlagsRecipe<Fluid>>)(Class<?>)IntrinsicFlagsRecipe.class;
    public static final RecipeType<IntrinsicFlagsRecipe<Fluid>> FLUID_RECIPE_TYPE = RecipeType.create(Petrolpark.MOD_ID, "fluid_intrinsic_flags", FLUID_INTRINSIC_FLAGS_RECIPE_CLASS);


    final IIngredientTypeWithSubtypes<INGREDIENT, STACK> ingredientType;
    
    public IntrinsicFlagsCategory(IGuiHelper guiHelper, ResourceLocation registryLocation, IIngredientTypeWithSubtypes<INGREDIENT, STACK> ingredientType, RecipeType<IntrinsicFlagsRecipe<INGREDIENT>> recipeType) {
        super(recipeType, createTitle(registryLocation), new FlagIngredientType.Icon(0xFFFF5555), 178, 110);
        this.ingredientType = ingredientType;
    };

    private static final Component createTitle(ResourceLocation registryLocation) {
		final String registryName = ResourceLocationUtil.sanitizePath(registryLocation.getPath());
		final String registryNameTranslationKey = Petrolpark.translationKey("gui.jei.category.intrinsicFlags." + registryName);

		Language language = Language.getInstance();
		if (language.has(registryNameTranslationKey)) return Component.translatable(registryNameTranslationKey);
		
		return Lang.translate("gui.jei.category.intrinsicFlags", StringUtils.capitalize(registryLocation.getPath()));
	};

    @Override
	public void setRecipe(IRecipeLayoutBuilder builder, IntrinsicFlagsRecipe<INGREDIENT> recipe, IFocusGroup focuses) {
        final List<STACK> stacks = recipe.ingredients().stream().map(ingredientType::getDefaultIngredient).toList();

		builder.addInputSlot(0, 0)
			.addIngredients(ingredientType, stacks)
			.setStandardSlotBackground();

        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(FlagIngredientType.TYPE, recipe.flag());

        builder.addOutputSlot(23, 4)
            .addIngredient(FlagIngredientType.TYPE, recipe.flag())
            .setBackground(FlagIngredientType.BACKGROUND, -1, -1)
            .setCustomRenderer(FlagIngredientType.TYPE, FlagIngredientType.FULL_RENDERER);
        builder
            .addInvisibleIngredients(RecipeIngredientRole.INPUT)
            .addIngredient(FlagIngredientType.TYPE, recipe.flag());

		for (STACK stack : stacks) {
			builder.addOutputSlot()
				.addIngredient(ingredientType, stack);
		};
	};

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IntrinsicFlagsRecipe<INGREDIENT> recipe, IFocusGroup focuses) {
		List<IRecipeSlotDrawable> outputSlots = builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT);
        outputSlots = outputSlots.subList(1, outputSlots.size());

		IScrollGridWidget scrollGridWidget = builder.addScrollGridWidget(outputSlots, 9, 5);
		scrollGridWidget.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    };

    public record IntrinsicFlagsRecipe<INGREDIENT>(FlagHolderHolder flag, List<INGREDIENT> ingredients) {};

    public static final <INGREDIENT> List<IntrinsicFlagsRecipe<INGREDIENT>> getAllRecipes(Registry<INGREDIENT> registry, Flaggable<INGREDIENT, ?> flaggable) {
        final HashMap<Holder<Flag>, List<INGREDIENT>> intrinsics = new HashMap<>();
        for (INGREDIENT ingredient : registry.stream().toList()) {
            final Collection<Holder<Flag>> intrinsicFlags = flaggable.getIntrinsicFlags(ingredient);
            if (intrinsicFlags == null) continue;
            for (Holder<Flag> flagHolder : intrinsicFlags) {
                final List<INGREDIENT> ingredientList = new ArrayList<>(1);
                ingredientList.add(ingredient);
                intrinsics.merge(flagHolder, ingredientList, (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                });
            };
        };
        return intrinsics.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .map(entry -> new IntrinsicFlagsRecipe<>(new FlagHolderHolder(entry.getKey()), entry.getValue()))
            .toList();
    };
};
