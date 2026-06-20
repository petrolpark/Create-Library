package com.petrolpark.registry;

import java.util.Optional;
import java.util.function.Supplier;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.data.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.core.world.item.decay.DecayingItemCookingRecipe;
import com.petrolpark.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

@Deprecated
public enum PetrolparkRecipeTypesOld implements IPetrolparkRecipeTypes {

    DECAYING_ITEM_COOKING(() -> DecayingItemCookingRecipe.SERIALIZER, () -> null, false), //TODO remove null
    ;

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final Supplier<RecipeType<?>> type;

    PetrolparkRecipeTypesOld(Supplier<RecipeSerializer<?>> serializerSupplier) {
        this(serializerSupplier, null, true);
    };

    PetrolparkRecipeTypesOld(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    PetrolparkRecipeTypesOld(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            if (typeSupplier == null) typeSupplier = () -> RecipeType.simple(Petrolpark.asResource(name()));
            type = Registers.TYPE_REGISTER.register(name, typeSupplier);
        } else {
            type = typeSupplier;
        };
    };

    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    public <S extends RecipeSerializer<?>> S getSerializer() {
        return (S) serializerObject.get();
    };

    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    };

    <I extends RecipeInput, R extends Recipe<I>> PetrolparkRecipeTypesOld(Class<R> recipeClass, Supplier<RecipeSerializer<R>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        type = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
    };

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I craftingInput, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), craftingInput, world);
    };

    public static final void register() {};
    
};
