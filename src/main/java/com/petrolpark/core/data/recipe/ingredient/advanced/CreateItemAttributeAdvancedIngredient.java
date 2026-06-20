package com.petrolpark.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.PetrolparkCreateTags;
import com.petrolpark.compat.create.RequiresCreate;
import com.petrolpark.compat.create.registry.PetrolparkCreateAdvancedIngredientTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;
import com.petrolpark.util.codec.CodecHelper;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@RequiresCreate
public record CreateItemAttributeAdvancedIngredient(ItemAttribute attribute) implements IAdvancedIngredient<ItemStack> {

    public static final MapCodec<CreateItemAttributeAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(ItemAttribute.CODEC, "attribute", CreateItemAttributeAdvancedIngredient::attribute,  CreateItemAttributeAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateItemAttributeAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(ItemAttribute.STREAM_CODEC, CreateItemAttributeAdvancedIngredient::attribute, CreateItemAttributeAdvancedIngredient::new);

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
    public INamedAdvancedIngredientType<ItemStack> getType() {
        return PetrolparkCreateAdvancedIngredientTypes.ITEM_ATTRIBUTE.get();
    };

    public static record Type(String translationKey) implements INamedAdvancedIngredientType<ItemStack> {

        @Override
        public MapCodec<CreateItemAttributeAdvancedIngredient> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreateItemAttributeAdvancedIngredient> streamCodec() {
            return STREAM_CODEC;
        };

        @Override
        public Stream<CreateItemAttributeAdvancedIngredient> streamApplicableIngredients(Level level, ItemStack stack) {
            return level.registryAccess().lookupOrThrow(CreateRegistries.ITEM_ATTRIBUTE_TYPE)
                .getOrThrow(PetrolparkCreateTags.ItemAttributes.LEVEL_INDEPDENDENT.tag)
                .stream()
                .filter(PetrolparkCreateTags.ItemAttributes.NOT_FOR_INGREDIENTS::matches)
                .map(Holder::value)
                .flatMap(iat -> iat.getAllAttributes(stack, level).stream())
                .map(CreateItemAttributeAdvancedIngredient::new);
        };

    };
    
};
