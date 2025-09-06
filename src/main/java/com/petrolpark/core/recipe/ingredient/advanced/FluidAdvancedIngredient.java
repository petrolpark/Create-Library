package com.petrolpark.core.recipe.ingredient.advanced;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidAdvancedIngredient extends IAdvancedIngredient<FluidStack> {
    
    /**
     * Use {@link FluidAdvancedIngredient#CODEC instead}.
     */
    static final Codec<IAdvancedIngredient<? super FluidStack>> TYPED_CODEC = PetrolparkRegistries.ADVANCED_FLUID_INGREDIENT_TYPES
        .byNameCodec()
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::codec);

    public static final Codec<IAdvancedIngredient<? super FluidStack>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassAdvancedIngredient.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super FluidStack>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE)
        .dispatch(IAdvancedIngredient::getType, IAdvancedIngredientType::streamCodec);

    default Component translate(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return Component.translatable(getType().translationKey() + ".inverse");
    };

    @Override
    public NamedAdvancedIngredientType<FluidStack> getType();
};
