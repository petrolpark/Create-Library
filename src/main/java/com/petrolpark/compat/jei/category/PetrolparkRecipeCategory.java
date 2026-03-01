package com.petrolpark.compat.jei.category;

import com.petrolpark.compat.jei.BiomeSpecificTooltipHelper;
import com.petrolpark.compat.jei.ingredient.BiomeIngredientType;
import com.petrolpark.core.recipe.IBiomeSpecificRecipe;
import com.petrolpark.core.recipe.book.IBookRequiredRecipe;
import com.petrolpark.core.recipe.book.RecipeBookItem;
import com.petrolpark.util.Lang;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public abstract class PetrolparkRecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

    protected final IJeiHelpers helpers;
    protected final Minecraft mc = Minecraft.getInstance();

    public PetrolparkRecipeCategory(CreateRecipeCategory.Info<T> info, IJeiHelpers helpers) {
        super(info);
        this.helpers = helpers;
    };

    public interface Factory<T extends Recipe<?>> {
		CreateRecipeCategory<T> create(CreateRecipeCategory.Info<T> info, IJeiHelpers helpers);
	};

    public static final void addOptionalRequiredBiomeSlot(IRecipeLayoutBuilder builder, Recipe<?> recipe, int x, int y) {
        if (!(recipe instanceof IBiomeSpecificRecipe biomeRecipe)) return;
        if (biomeRecipe.getAllowedBiomes().map(HolderSet::size).orElse(0) != 0) builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(BiomeIngredientType.TYPE, BiomeSpecificTooltipHelper.getAllBiomes(biomeRecipe).toList())
            .addRichTooltipCallback(BiomeSpecificTooltipHelper.getAllowedBiomeList(biomeRecipe)); 
    };

    public final void addOptionalRecipeBookSlot(IRecipeLayoutBuilder builder, RecipeHolder<?> recipeHolder, int x, int y) {
        addOptionalRecipeBookSlot(getRecipeType(), builder, recipeHolder, x, y);
    };

    public static final void addOptionalRecipeBookSlot(RecipeType<?> recipeType, IRecipeLayoutBuilder builder, RecipeHolder<?> recipeHolder, int x, int y) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (!(recipeHolder.value() instanceof IBookRequiredRecipe bookRecipe) || level == null || !bookRecipe.isBookRequired(level)) return;
        builder.addInputSlot(x, y)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(RecipeBookItem.of(recipeHolder, recipeType.getUid()))
            .addRichTooltipCallback((view, tooltip) -> tooltip.add(Lang.translate("recipe.book_required").withStyle(ChatFormatting.GOLD)));
    };;

    protected RegistryAccess getRegistryAccess() {
        ClientLevel level = mc.level;
        if (level == null) throw new IllegalStateException("Cannot get Registry Access outside gameplay");
        return level.registryAccess();
    };
    
};
