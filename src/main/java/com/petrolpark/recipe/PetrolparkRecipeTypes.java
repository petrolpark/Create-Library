package com.petrolpark.recipe;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.Petrolpark;
import com.petrolpark.badge.BadgeDuplicationRecipe;
import com.petrolpark.itemdecay.recipe.DecayingItemCookingRecipeSerializer;
import com.petrolpark.recipe.advancedprocessing.AdvancedProcessingRecipeSerializer;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public enum PetrolparkRecipeTypes implements IRecipeTypeInfo {

    DECAYING_ITEM_COOKING(DecayingItemCookingRecipeSerializer::new),
    BADGE_DUPLICATION(() -> BadgeDuplicationRecipe.BADGE_DUPLICATION, () -> RecipeType.CRAFTING);

    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    @Nullable
    private final RegistryObject<RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this(serializerSupplier, typeSupplier, false);
    };

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
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

    PetrolparkRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Petrolpark.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    };

    PetrolparkRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new AdvancedProcessingRecipeSerializer<>(processingFactory));
    };

    PetrolparkRecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory, Supplier<RecipeType<?>> typeSupplier) {
        this(() -> new AdvancedProcessingRecipeSerializer<>(processingFactory), typeSupplier);
    };

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    };

    @Override
    public ResourceLocation getId() {
        return id;
    };

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    };

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type.get();
    };

    public boolean is(Recipe<?> recipe) {
        return recipe.getType() == this.getType();
    };

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
            .getRecipeFor(getType(), inv, world);
    };

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Petrolpark.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, Petrolpark.MOD_ID);
    };
    
};
