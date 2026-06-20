package com.petrolpark.compat.create.registry;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipe;
import com.petrolpark.compat.create.core.data.recipe.AdvancedProcessingRecipeParams;
import com.petrolpark.compat.create.core.data.recipe.RecipeBookMechanicalCraftingRecipe;
import com.petrolpark.compat.create.core.world.dough.cookieCutter.CookieCuttingRecipe;
import com.petrolpark.core.data.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.shared.world.item.crafting.SharedRecipeType;
import com.petrolpark.util.Lang;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

public enum PetrolparkCreateRecipeTypes implements IPetrolparkRecipeTypes, IRecipeTypeInfo {

    COOKIE_CUTTING(CookieCuttingRecipe.Serializer::new, () -> CookieCuttingRecipe.TYPE),
    RECIPE_BOOK_MECHANICAL_CRAFTING(RecipeBookMechanicalCraftingRecipe.Serializer::new),
    ;

    /**
     * The ResourceLocation of both the Serializer, and Type (if the Type is registered here).
     */
    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final @Nullable DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    /**
     * Create the Serializer and Type similar to how Create does it, just with {@link AdvancedProcessingRecipe} Params and Serializers.
     * @param <R> Type of the Advanced Processing Recipe
     * @param processingFactory
     */
    <R extends AdvancedProcessingRecipe<?>> PetrolparkCreateRecipeTypes(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory) {
        this(() -> new AdvancedProcessingRecipe.Serializer<>(processingFactory));
    };

    /**
     * Create the Serializer and Type similar to how Create does it, just with {@link AdvancedProcessingRecipe} Params and Serializers.
     * @param <R> Type of the Advanced Processing Recipe
     * @param processingFactory
     */
    <R extends AdvancedProcessingRecipe<?>> PetrolparkCreateRecipeTypes(SharedFeatureFlag featureFlag, ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory) {
        this(featureFlag, () -> new AdvancedProcessingRecipe.Serializer<>(processingFactory));
    };

    PetrolparkCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    PetrolparkCreateRecipeTypes(SharedFeatureFlag featureFlag, Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> new SharedRecipeType<>(id, featureFlag));
        type = typeObject;
    };

    <R extends AdvancedProcessingRecipe<?>> PetrolparkCreateRecipeTypes(ProcessingRecipe.Factory<AdvancedProcessingRecipeParams, R> processingFactory, Supplier<RecipeType<?>> typeSupplier) {
        this(() -> new AdvancedProcessingRecipe.Serializer<>(processingFactory), typeSupplier);
    };

    PetrolparkCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    PetrolparkCreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            type = typeObject;
        } else {
            typeObject = null;
            type = typeSupplier;
        };
    };

    /**
     * The ResourceLocation of both the Serializer, and Type (if the Type is registered here).
     */
    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    };

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    };

    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType(Class<R> recipeClass) {
        return (RecipeType<R>) type.get();
    };

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> find(I inv, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), inv, world);
    };

    public static final void init() {};
    
};
