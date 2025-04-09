package com.petrolpark.core.recipe.ingredient.modifier;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public interface ItemIngredientModifier extends IIngredientModifier<ItemStack> {

    /**
     * Use {@link ItemIngredientModifier#CODEC instead}.
     */
    static final Codec<IIngredientModifier<? super ItemStack>> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_MODIFIER_TYPES
        .byNameCodec()
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::codec);

    public static final Codec<IIngredientModifier<? super ItemStack>> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassIngredientModifier.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super ItemStack>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE)
        .dispatch(IIngredientModifier::getType, IIngredientModifierType::streamCodec);

    @Override
    public boolean test(ItemStack stack);

    default Component translate(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return Component.translatable(getType().translationKey() + ".inverse");
    };

    @Override
    public IngredientModifierType<ItemStack> getType();
};
