package com.petrolpark.compat.create;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.common.processing.basinlid.LiddedBasinRecipe;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.create.common.processing.mandrel.MandrelRecipe;
import com.petrolpark.core.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.util.Lang;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

public enum CreateRecipeTypes implements IPetrolparkRecipeTypes, IRecipeTypeInfo {

    EXTRUSION(ExtrusionRecipe.Serializer::new),
    LIDDED_BASIN(LiddedBasinRecipe.Serializer::new),
    MANDREL(MandrelRecipe.Serializer::new)
    //FIRST_TIME_LUCKY_MILLING(FTLMillingRecipe::new, AllRecipeTypes.MILLING::getType),
    ;

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    CreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    // CreateRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
    //     this(() -> new AdvancedProcessingRecipeSerializer<>(processingFactory));
    // };

    // CreateRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory, Supplier<RecipeType<?>> typeSupplier) {
    //     this(() -> new AdvancedProcessingRecipeSerializer<>(processingFactory), typeSupplier);
    // };

    CreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    CreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
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
        return world.getRecipeManager()
            .getRecipeFor(getType(), inv, world);
    };

    public static final void init() {};
    
};
