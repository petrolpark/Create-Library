package com.petrolpark.core.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkAdvancedIngredientTypes;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ItemItemAdvancedIngredient(Item item) implements ItemAdvancedIngredient {

    public static final MapCodec<ItemItemAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(BuiltInRegistries.ITEM.byNameCodec(), "id", ItemItemAdvancedIngredient::item, ItemItemAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemItemAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.registry(Registries.ITEM), ItemItemAdvancedIngredient::item, ItemItemAdvancedIngredient::new);

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(item());
    };

    @Override
    public Stream<ItemStack> streamExamples() {
        return Stream.of(new ItemStack(item()));
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(item().getDescription());
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(item().getDescription()));
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkAdvancedIngredientTypes.ITEM.get();
    };
    
    public static record Type(String translationKey) implements INamedAdvancedIngredientType<ItemStack> {

        @Override
        public MapCodec<ItemItemAdvancedIngredient> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemItemAdvancedIngredient> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<ItemItemAdvancedIngredient> streamApplicableIngredients(Level level, ItemStack stack) {
            return Stream.of(new ItemItemAdvancedIngredient(stack.getItem()));
        };

    };
    
};
