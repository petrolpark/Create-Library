package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IngredientModifier extends LootContextUser {

    /**
     * Use {@link IngredientModifier#CODEC instead}.
     */
    static final Codec<IngredientModifier> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_MODIFIER_TYPE
        .byNameCodec()
        .dispatch(IngredientModifier::getType, IngredientModifierType::codec);

    public static final Codec<IngredientModifier> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(PassIngredientModifier.INSTANCE)));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientModifier> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE)
        .dispatch(IngredientModifier::getType, IngredientModifierType::streamCodec);

    public boolean test(ItemStack stack);

    public void modifyExamples(List<ItemStack> exampleStacks);

    public void modifyCounterExamples(List<ItemStack> counterExampleStacks);

    @OnlyIn(Dist.CLIENT)
    public void addToDescription(List<Component> description);

    @OnlyIn(Dist.CLIENT)
    public void addToCounterDescription(List<Component> description);

    default Component translate(Object... translationArgs) {
        return Component.translatable(getType().translationKey());
    };

    default Component translateInverse(Object... translationArgs) {
        return Component.translatable(getType().translationKey() + ".inverse");
    };

    public IngredientModifierType getType();
};
