package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidIngredientModifier extends IIngredientModifier<FluidStack> {
    
    /**
     * Use {@link FluidIngredientModifier#CODEC instead}.
     */
    static final Codec<IIngredientModifier<? super FluidStack>> TYPED_CODEC = PetrolparkRegistries.FLUID_INGREDIENT_MODIFIER_TYPES
        .byNameCodec()
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::codec);

    public static final Codec<IIngredientModifier<? super FluidStack>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassIngredientModifier.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super FluidStack>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.FLUID_INGREDIENT_MODIFIER_TYPE)
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::streamCodec);

    default Component translate(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return Component.translatable(getType().translationKey() + ".inverse");
    };

    @Override
    public IngredientModifierType<FluidStack> getType();
};
