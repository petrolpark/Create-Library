package com.petrolpark.core.recipe.ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientTypes;
import com.petrolpark.core.recipe.ingredient.modifier.IngredientModifier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record ModifiedIngredient(Ingredient ingredient, List<IngredientModifier> modifiers) implements ICustomIngredient {

    public static final MapCodec<ModifiedIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(ModifiedIngredient::ingredient),
        IngredientModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifiedIngredient::modifiers)
    ).apply(instance, ModifiedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedIngredient> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, ModifiedIngredient::ingredient,
        IngredientModifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ModifiedIngredient::modifiers,
        ModifiedIngredient::new
    );

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        if (!ingredient.test(stack)) return false;
        for (IngredientModifier modifier : modifiers()) {
            if (!modifier.test(stack)) return false;
        };
        return true;
    };

    @Override
    public Stream<ItemStack> getItems() {
        List<ItemStack> items = Arrays.asList(ingredient.getItems());
        modifiers().forEach(modifier -> modifier.modifyExamples(items));
        return items.stream();
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
