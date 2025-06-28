package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkIngredientModifierTypes;
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

public record ItemItemIngredientModifier(Item item) implements ItemIngredientModifier {

    public static final MapCodec<ItemItemIngredientModifier> CODEC = CodecHelper.singleFieldMap(BuiltInRegistries.ITEM.byNameCodec(), "id", ItemItemIngredientModifier::item, ItemItemIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemItemIngredientModifier> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.registry(Registries.ITEM), ItemItemIngredientModifier::item, ItemItemIngredientModifier::new);

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
    public INamedIngredientModifierType<ItemStack> getType() {
        return PetrolparkIngredientModifierTypes.ITEM.get();
    };
    
    public static record Type(String translationKey) implements INamedIngredientModifierType<ItemStack> {

        @Override
        public MapCodec<ItemItemIngredientModifier> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemItemIngredientModifier> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<ItemItemIngredientModifier> streamApplicableModifiers(Level level, ItemStack stack) {
            return Stream.of(new ItemItemIngredientModifier(stack.getItem()));
        };

    };
    
};
