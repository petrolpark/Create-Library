package com.petrolpark.compat.jei.category.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.foundation.config.ConfigBase.ConfigBool;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

/**
 * Used to generate JEI Categories for Petrolpark mods.
 * Basically all copied from the {@link com.simibubi.create.compat.jei.CreateJEI.CategoryBuilder Create source code}.
 */
public class PetrolparkCategoryBuilder<T extends Recipe<?>> {

    public static IJeiHelpers helpers;

    private final String modid;
    private final Consumer<CreateRecipeCategory<?>> categoryAdder;
    private final Class<? extends T> recipeClass;

    private IDrawable background;
    private IDrawable icon;

    private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
    private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

    private Predicate<CRecipes> createConfigPredicate;

    public PetrolparkCategoryBuilder(String modid, Class<? extends T> recipeClass, Consumer<CreateRecipeCategory<?>> categoryAdder) {
        this.modid = modid;
        this.categoryAdder = categoryAdder;
        this.recipeClass = recipeClass;
        createConfigPredicate = c -> true;
    };

    /**
     * Adds a List of Recipes to this Category.
     * @param collection The List of Recipes
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
        recipeListConsumers.add(recipes -> recipes.addAll(collection.get()));
        Petrolpark.LOGGER.info("Loaded " + collection.get().size()+ " recipes of type " + recipeClass.getSimpleName()+ ".");
        return this;
    };

    /**
     * Adds all Recipes of a given Recipe Type to this Category.
     * @param recipeTypeEntry The Recipe Type
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
        recipeListConsumers.add(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeTypeEntry.getType()));
        return this;
    };

    /**
     * Adds all Recipes of a given Recipe Type to this Category, given that each recipe matches the given condition.
     * @param recipeTypeEntry The Recipe Type
     * @param pred The Condition a Recipe must match to be added
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<? extends T>> recipeType, Predicate<Recipe<?>> pred) {
        recipeListConsumers.add(recipes -> CreateJEI.<T>consumeTypedRecipes(recipe -> {
            if (pred.test(recipe)) recipes.add(recipe);
        }, recipeType.get()));
        return this;
    };

    /**
     * Transforms all Recipes of a given Type to the correct Recipe amd then adds them to this Category.
     * @param recipeType
     * @param recipeTransformer Turns a Recipe into another
     * @param pred Whether to transform and then add a Recipe
     * @return This Category Builder
     */
    public <R extends Recipe<?>> PetrolparkCategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<R>> recipeType, Function<R, ? extends T> recipeTransformer, Predicate<R> pred) {
        recipeListConsumers.add(recipes -> CreateJEI.<R>consumeTypedRecipes(recipe -> {
            if (pred.test(recipe)) recipes.add(recipeTransformer.apply(recipe));
        }, recipeType.get()));
        return this;
    };

    /**
     * Adds a given collection of Item Stacks as catalysts for all Recipes of this Category.
     * @param itemSupplier A collection of suppliers of catalyst Item Stacks
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> catalysts(Collection<Supplier<ItemStack>> catalystStackSuppliers) {
        catalysts.addAll(catalystStackSuppliers);
        return this;
    };

    /**
     * Adds a given Item as a Catalyst for all Recipes of this Category.
     * Useful for adding the required machines for a Category of Recipe.
     * @param itemSupplier A Supplier of the catalyst Item
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> catalyst(Supplier<ItemLike> itemSupplier) {
        catalysts.add(() -> new ItemStack(itemSupplier.get().asItem()));
        return this;
    };

    /**
     * Sets the given Item as the icon for this Category.
     * @param item Typically this will be the machine required for this Type of Recipe
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> itemIcon(ItemLike item) {
        return itemIcon(() -> new ItemStack(item));
    };

    /**
     * Sets the given Item as the icon for this Category.
     * @param item Typically this will be the machine required for this Type of Recipe
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> itemIcon(Supplier<ItemStack> item) {
        this.icon = new ItemIcon(item);
        return this;  
    };

    /**
     * Sets the given pair of Items as the icon for this Category.
     * @param bigItem Typically this will be the machine required for this Type of Recipe
     * @param smallItem Typically this will be a way to differentiate this use from other uses of the same machine
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> doubleItemIcon(ItemLike bigItem, ItemLike smallItem) {
        return doubleItemIcon(() -> new ItemStack(bigItem), () -> new ItemStack(smallItem));
    };

    /**
     * Sets the given pair of Items as the icon for this Category.
     * @param bigItem Typically this will be the machine required for this Type of Recipe
     * @param smallItem Typically this will be a way to differentiate this use from other uses of the same machine
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> doubleItemIcon(Supplier<ItemStack> bigItem, Supplier<ItemStack> smallItem) {
        this.icon = new DoubleItemIcon(bigItem, smallItem);
        return this;
    };

    // /**
    //  * Sets the given triplet of Items as the icon for this Category.
    //  * @param bigItem
    //  * @param leftSmallItem
    //  * @param rightSmallItem
    //  * @return This Category Builder
    //  */
    // public CategoryBuilder<T> tripleItemIcon(Supplier<ItemStack> bigItem, Supplier<ItemStack> leftSmallItem, Supplier<ItemStack> rightSmallItem) {
    //     this.icon = new TripleItemIcon(bigItem, rightSmallItem, leftSmallItem);
    //     return this;
    // };

    /**
     * Sets the size of the Background for this Category.
     * @param width
     * @param height
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> emptyBackground(int width, int height) {
        this.background = new EmptyBackground(width, height);
        return this;
    };

    /**
     * Only enable this Recipe Category if the given test of Create's config options are true.
     * @return This Category Builder
     */
    public PetrolparkCategoryBuilder<T> enableIfCreateConfig(Function<CRecipes, ConfigBool> configValue) {
        createConfigPredicate = c -> configValue.apply(c).get();
        return this;
    };

    /**
     * Builds this Category.
     * @param name The Resource Location (e.g. for use in language file)
     * @param factory Initializer of the Category class
     * @return This Category
     */
    public CreateRecipeCategory<T> build(String name, PetrolparkRecipeCategory.Factory<T> factory) {
        Supplier<List<T>> recipesSupplier;
        if (createConfigPredicate.test(AllConfigs.server().recipes)) {
            recipesSupplier = () -> {
                List<T> recipes = new ArrayList<>();
                for (Consumer<List<T>> consumer : recipeListConsumers)
                    consumer.accept(recipes);
                return recipes;
            };
        } else {
            recipesSupplier = () -> Collections.emptyList();
        };

        mezz.jei.api.recipe.RecipeType<T> type = new mezz.jei.api.recipe.RecipeType<T>(new ResourceLocation(modid, name), recipeClass);

        Info<T> info = new Info<T>(
            type,
            Component.translatable(modid+".recipe."+name),
            background,
            icon,
            recipesSupplier,
            catalysts
        );

        CreateRecipeCategory<T> category = factory.create(info, helpers);
        categoryAdder.accept(category);

        if (category instanceof ITickableCategory tickableCategory) ITickableCategory.TICKING_CATEGORIES.add(tickableCategory);

        return category;
    };

    protected void finalizeBuilding(mezz.jei.api.recipe.RecipeType<T> type, CreateRecipeCategory<T> category, Class<? extends T> trueClass) {

    };
};
