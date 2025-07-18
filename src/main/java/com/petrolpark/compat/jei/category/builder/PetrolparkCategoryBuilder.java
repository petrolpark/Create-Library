package com.petrolpark.compat.jei.category.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import net.createmod.catnip.config.ConfigBase.ConfigBool;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

/**
 * Used to generate JEI Categories for Petrolpark mods.
 * Basically all copied from the {@link com.simibubi.create.compat.jei.CreateJEI.CategoryBuilder Create source code}.
 */
@RequiresCreate
public class PetrolparkCategoryBuilder<R extends Recipe<?>, C extends PetrolparkCategoryBuilder<R, C>> {

    public static IJeiHelpers helpers;

    protected final String modid;
    protected final Consumer<CreateRecipeCategory<?>> categoryAdder;
    protected final Class<? extends R> recipeClass;

    protected IDrawable background;
    protected IDrawable icon;

    protected final List<Consumer<List<RecipeHolder<R>>>> recipeListConsumers = new ArrayList<>();
    protected final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

    protected Predicate<CRecipes> createConfigPredicate;

    public PetrolparkCategoryBuilder(String modid, Class<? extends R> recipeClass, Consumer<CreateRecipeCategory<?>> categoryAdder) {
        this.modid = modid;
        this.categoryAdder = categoryAdder;
        this.recipeClass = recipeClass;
        createConfigPredicate = c -> true;
    };

    @SuppressWarnings("unchecked")
    private final C self() {
        return (C)this;
    };

    /**
     * Adds a List of Recipes to this Category.
     * @param collection The List of Recipes
     * @return This Category Builder
     */
    public C addRecipes(Supplier<Collection<? extends RecipeHolder<R>>> collection) {
        recipeListConsumers.add(recipes -> recipes.addAll(collection.get()));
        return self();
    };

    /**
     * Adds all Recipes of a given Recipe Type to this Category.
     * @param recipeTypeEntry The Recipe Type
     * @return This Category Builder
     */
    @SuppressWarnings("unchecked")
    public C addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
        recipeListConsumers.add(recipes -> CreateJEI.<R>consumeTypedRecipes(recipe -> {
            if (recipeClass.isInstance(recipe.value())) recipes.add((RecipeHolder<R>) recipe);
        }, recipeTypeEntry.getType()));
        return self();
    };

    /**
     * Adds all Recipes of a given Recipe Type to this Category
     * @param recipeType The Recipe Type
     * @return This Category Builder
     */
    public C addTypedRecipes(Supplier<RecipeType<? extends R>> recipeType) {
        return addTypedRecipesIf(recipeType, rh -> true);
    };

    /**
     * Adds all Recipes of a given Recipe Type to this Category, given that each recipe matches the given condition.
     * @param recipeType The Recipe Type
     * @param pred The Condition a Recipe must match to be added
     * @return This Category Builder
     */
    public C addTypedRecipesIf(Supplier<RecipeType<? extends R>> recipeType, Predicate<RecipeHolder<R>> pred) {
        recipeListConsumers.add(recipes -> consumeTypedRecipesTyped(recipe -> {
            if (pred.test(recipe)) recipes.add(recipe);
        }, recipeType.get()));
        return self();
    };

    /**
     * Transforms all Recipes of a given Type to the correct Recipe amd then adds them to this Category.
     * @param recipeType
     * @param recipeTransformer Turns a Recipe into another
     * @param pred Whether to transform and then add a Recipe
     * @return This Category Builder
     */
    public <R2 extends Recipe<?>> C addTypedRecipesIf(Supplier<RecipeType<R2>> recipeType, Function<RecipeHolder<?>, RecipeHolder<R>> recipeTransformer, Predicate<RecipeHolder<?>> pred) {
        recipeListConsumers.add(recipes -> CreateJEI.<R2>consumeTypedRecipes(recipe -> {
            if (pred.test(recipe)) recipes.add(recipeTransformer.apply(recipe));
        }, recipeType.get()));
        return self();
    };

    /**
     * Adds a given collection of Item Stacks as catalysts for all Recipes of this Category.
     * @param catalystStackSuppliers A collection of suppliers of catalyst Item Stacks
     * @return This Category Builder
     */
    public C catalysts(Collection<Supplier<ItemStack>> catalystStackSuppliers) {
        catalysts.addAll(catalystStackSuppliers);
        return self();
    };

    /**
     * Adds a given Item as a Catalyst for all Recipes of this Category.
     * Useful for adding the required machines for a Category of Recipe.
     * @param itemSupplier A Supplier of the catalyst Item
     * @return This Category Builder
     */
    public C catalyst(Supplier<ItemLike> itemSupplier) {
        catalysts.add(() -> new ItemStack(itemSupplier.get().asItem()));
        return self();
    };

    /**
     * Sets the given Item as the icon for this Category.
     * @param item Typically this will be the machine required for this Type of Recipe
     * @return This Category Builder
     */
    public C itemIcon(ItemLike item) {
        return itemIcon(() -> new ItemStack(item));
    };

    /**
     * Sets the given Item as the icon for this Category.
     * @param item Typically this will be the machine required for this Type of Recipe
     * @return This Category Builder
     */
    public C itemIcon(Supplier<ItemStack> item) {
        this.icon = new ItemIcon(item);
        return self();  
    };

    /**
     * Sets the given pair of Items as the icon for this Category.
     * @param bigItem Typically this will be the machine required for this Type of Recipe
     * @param smallItem Typically this will be a way to differentiate this use from other uses of the same machine
     * @return This Category Builder
     */
    public C doubleItemIcon(ItemLike bigItem, ItemLike smallItem) {
        return doubleItemIcon(() -> new ItemStack(bigItem), () -> new ItemStack(smallItem));
    };

    /**
     * Sets the given pair of Items as the icon for this Category.
     * @param bigItem Typically this will be the machine required for this Type of Recipe
     * @param smallItem Typically this will be a way to differentiate this use from other uses of the same machine
     * @return This Category Builder
     */
    public C doubleItemIcon(Supplier<ItemStack> bigItem, Supplier<ItemStack> smallItem) {
        this.icon = new DoubleItemIcon(bigItem, smallItem);
        return self();
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
    public C emptyBackground(int width, int height) {
        this.background = new EmptyBackground(width, height);
        return self();
    };

    /**
     * Only enable this Recipe Category if the given test of Create's config options are true.
     * @return This Category Builder
     */
    public C enableIfCreateConfig(Function<CRecipes, ConfigBool> configValue) {
        createConfigPredicate = c -> configValue.apply(c).get();
        return self();
    };

    // @SuppressWarnings("unchecked")
    // private void consumeAllRecipesOfType(Consumer<RecipeHolder<R>> consumer) {
    //     CreateJEI.consumeAllRecipes(recipeHolder -> {
    //         if (recipeClass.isInstance(recipeHolder.value())) {
    //             //noinspection unchecked - this is checked by the if statement
    //             consumer.accept((RecipeHolder<R>) recipeHolder);
    //         };
    //     });
    // };

    @SuppressWarnings("unchecked")
    private void consumeTypedRecipesTyped(Consumer<RecipeHolder<R>> consumer, RecipeType<?> type) {
        CreateJEI.consumeTypedRecipes(recipeHolder -> {
            if (recipeClass.isInstance(recipeHolder.value())) {
                //noinspection unchecked - this is checked by the if statement
                consumer.accept((RecipeHolder<R>) recipeHolder);
            }
        }, type);
    };

    /**
     * Builds this Category.
     * @param name The Resource Location (e.g. for use in language file)
     * @param factory Initializer of the Category class
     * @return This Category
     */
    public CreateRecipeCategory<R> build(String name, PetrolparkRecipeCategory.Factory<R> factory) {
        Supplier<List<RecipeHolder<R>>> recipesSupplier;
        if (createConfigPredicate.test(AllConfigs.server().recipes)) {
            recipesSupplier = () -> {
                List<RecipeHolder<R>> recipes = new ArrayList<>();
                for (Consumer<List<RecipeHolder<R>>> consumer : recipeListConsumers)
                    consumer.accept(recipes);
                return recipes;
            };
        } else {
            recipesSupplier = () -> Collections.emptyList();
        };

        mezz.jei.api.recipe.RecipeType<R> type = new mezz.jei.api.recipe.RecipeType<R>(ResourceLocation.fromNamespaceAndPath(modid, name), recipeClass);

        Info<R> info = new Info<R>(
            mezz.jei.api.recipe.RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(modid, name)),
            Component.translatable(modid+".recipe."+name),
            background,
            icon,
            recipesSupplier,
            catalysts
        );

        CreateRecipeCategory<R> category = factory.create(info, helpers);
        finalizeBuilding(type, category, recipeClass);
        categoryAdder.accept(category);

        if (category instanceof ITickableCategory tickableCategory) ITickableCategory.TICKING_CATEGORIES.add(tickableCategory);

        return category;
    };

    protected void finalizeBuilding(mezz.jei.api.recipe.RecipeType<R> type, CreateRecipeCategory<R> category, Class<? extends R> trueClass) {};
};
