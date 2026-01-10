package com.petrolpark.core.item.decay.ageing;

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

public record AgeingRecipe(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime) implements IApplyDecayRecipe {

    public static final MapCodec<AgeingRecipe> CODEC = IApplyDecayRecipe.codec(AgeingRecipe::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, AgeingRecipe> STREAM_CODEC = IApplyDecayRecipe.streamCodec(AgeingRecipe::new);

    @Override
    public RecipeSerializer<AgeingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.AGEING.get();
    };

    @Override
    public RecipeType<AgeingRecipe> getType() {
        return PetrolparkRecipeTypes.AGEING.get();
    };
};
