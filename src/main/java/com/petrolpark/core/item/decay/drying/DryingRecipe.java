package com.petrolpark.core.item.decay.drying;

import com.mojang.serialization.MapCodec;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRecipeSerializers;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.DecayTime;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.decay.product.ChangeItemDecayProduct;
import com.petrolpark.core.item.decay.product.IDecayProduct;

import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public record DryingRecipe(Ingredient ingredient, IDecayProduct decayProduct, DecayTime decayTime) implements IApplyDecayRecipe {

    public static final MapCodec<DryingRecipe> CODEC = IApplyDecayRecipe.codec(DryingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = IApplyDecayRecipe.streamCodec(DryingRecipe::new);

    public static final String DRYING_TRANSLATION_KEY = Util.makeDescriptionId("item", Petrolpark.asResource("drying_item.remaining"));

    public static final DryingRecipe of(Ingredient ingredient, ItemLike result, int decayTime) {
        return new DryingRecipe(ingredient, ChangeItemDecayProduct.of(result), new DecayTime(DRYING_TRANSLATION_KEY, decayTime));
    };

    @Override
    public RecipeSerializer<DryingRecipe> getSerializer() {
        return PetrolparkRecipeSerializers.DRYING.get();
    };

    @Override
    public RecipeType<DryingRecipe> getType() {
        return PetrolparkRecipeTypes.DRYING.get();
    };

};
