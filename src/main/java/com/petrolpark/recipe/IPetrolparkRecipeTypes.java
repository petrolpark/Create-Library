package com.petrolpark.recipe;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public interface IPetrolparkRecipeTypes {

    public static void register(IEventBus modEventBus) {
        PetrolparkRecipeTypes.register();
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    };

    public ResourceLocation getId();

    public <T extends RecipeSerializer<?>> T getSerializer();

    public <T extends RecipeType<?>> T getType();

    public default boolean is(Recipe<?> recipe) {
        return recipe.getType() == this.getType();
    };
    
    public static class Registers {
        public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Petrolpark.MOD_ID);
        public static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Petrolpark.MOD_ID);
    };
};
