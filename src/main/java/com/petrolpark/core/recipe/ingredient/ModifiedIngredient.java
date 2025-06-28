package com.petrolpark.core.recipe.ingredient;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientTypes;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ItemIngredientModifier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record ModifiedIngredient(IIngredientModifier<? super ItemStack> modifier) implements ICustomIngredient {

    public static final MapCodec<ModifiedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemIngredientModifier.CODEC.fieldOf("modifier").forGetter(ModifiedIngredient::modifier)
    ).apply(instance, ModifiedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedIngredient> STREAM_CODEC = StreamCodec.composite(
        ItemIngredientModifier.STREAM_CODEC, ModifiedIngredient::modifier,
        ModifiedIngredient::new
    );

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        return modifier().test(stack);
    };

    @Override
    public Stream<ItemStack> getItems() {
        return modifier().streamExamples().map(s -> s instanceof ItemStack stack ? stack : null);
    };

    @Override
    public boolean isSimple() {
        return false;
    };

    @Override
    public IngredientType<ModifiedIngredient> getType() {
        return PetrolparkIngredientTypes.MODIFIED.get();
    };
    
};
