package com.petrolpark.core.recipe.ingredient.modifier;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.CreateIngredientModifierTypes;
import com.petrolpark.compat.create.CreateTags;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@RequiresCreate
public record CreateItemAttributeIngredientModifier(ItemAttribute attribute) implements IIngredientModifier<ItemStack> {

    public static final MapCodec<CreateItemAttributeIngredientModifier> CODEC = CodecHelper.singleFieldMap(ItemAttribute.CODEC, "attribute", CreateItemAttributeIngredientModifier::attribute,  CreateItemAttributeIngredientModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateItemAttributeIngredientModifier> STREAM_CODEC = StreamCodec.composite(ItemAttribute.STREAM_CODEC, CreateItemAttributeIngredientModifier::attribute, CreateItemAttributeIngredientModifier::new);

    @Override
    public boolean test(ItemStack stack) {
        try {
            return attribute.appliesTo(stack, null); // Attribute must be able to handle null level
        } catch (NullPointerException e) {
            return false; // If it couldn't handle the null world don't match
        }
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        description.add(attribute().format(false));
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        description.add(attribute().format(true));
    };

    @Override
    public INamedIngredientModifierType<ItemStack> getType() {
        return CreateIngredientModifierTypes.ITEM_ATTRIBUTE.get();
    };

    public static record Type(String translationKey) implements INamedIngredientModifierType<ItemStack> {

        @Override
        public MapCodec<CreateItemAttributeIngredientModifier> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreateItemAttributeIngredientModifier> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<CreateItemAttributeIngredientModifier> streamApplicableModifiers(Level level, ItemStack stack) {
            return level.registryAccess().lookupOrThrow(CreateRegistries.ITEM_ATTRIBUTE_TYPE)
                .getOrThrow(CreateTags.ItemAttributes.LEVEL_INDEPDENDENT.tag)
                .stream()
                .filter(CreateTags.ItemAttributes.NOT_FOR_INGREDIENTS::matches)
                .map(Holder::value)
                .flatMap(iat -> iat.getAllAttributes(stack, level).stream())
                .map(CreateItemAttributeIngredientModifier::new);
        };

    };
    
};
