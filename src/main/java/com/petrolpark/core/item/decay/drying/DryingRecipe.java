package com.petrolpark.core.item.decay.drying;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.DecayTime;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.decay.product.IDecayProduct;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record DryingRecipe(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime) implements IApplyDecayRecipe {

    public static final MapCodec<DryingRecipe> CODEC = IApplyDecayRecipe.codec(DryingRecipe::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = IApplyDecayRecipe.streamCodec(DryingRecipe::new);

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.DRYING.get();
    };

    @Override
    public RecipeType<DryingRecipe> getType() {
        return PetrolparkRecipeTypes.DRYING.get();
    };
};
