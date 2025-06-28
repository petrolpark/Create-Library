package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkIngredientModifierTypes;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record TagItemIngredientModifier(TagKey<Item> tag) implements ItemIngredientModifier {

    public static final MapCodec<TagItemIngredientModifier> CODEC = CodecHelper.singleFieldMap(TagKey.codec(Registries.ITEM), "tag", TagItemIngredientModifier::tag, TagItemIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, TagItemIngredientModifier> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.map(rl -> TagKey.create(Registries.ITEM, rl), TagKey::location), TagItemIngredientModifier::tag,
        TagItemIngredientModifier::new
    );

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(tag());
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(translateSimple(Lang.tag(tag())));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(translateInverse(Lang.tag(tag())));
    };

    @Override
    public INamedIngredientModifierType<ItemStack> getType() {
        return PetrolparkIngredientModifierTypes.ITEM_TAG.get();
    };

    public static record Type(String translationKey) implements INamedIngredientModifierType<ItemStack> {

        @Override
        public MapCodec<TagItemIngredientModifier> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf,TagItemIngredientModifier> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<TagItemIngredientModifier> streamApplicableModifiers(Level level, ItemStack stack) {
            return PetrolparkRegistries.getHolder(level.registryAccess(), Registries.ITEM, stack.getItem())
                .stream()
                .flatMap(Holder::tags)
                .map(TagItemIngredientModifier::new);
        };

    };
    
};
