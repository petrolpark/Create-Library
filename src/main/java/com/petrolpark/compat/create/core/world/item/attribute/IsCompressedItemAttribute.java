package com.petrolpark.compat.create.core.world.item.attribute;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.registry.PetrolparkItemAttributeTypes;
import com.petrolpark.core.world.item.compression.IItemCompressionSequence;
import com.petrolpark.core.world.item.compression.ItemCompressionManager;
import com.petrolpark.util.codec.CodecHelper;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record IsCompressedItemAttribute(ItemStack baseItem) implements PetrolparkItemAttribute {

    public static final MapCodec<IsCompressedItemAttribute> CODEC = CodecHelper.singleFieldMap(ItemStack.SINGLE_ITEM_CODEC, "base_item", IsCompressedItemAttribute::baseItem, IsCompressedItemAttribute::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, IsCompressedItemAttribute> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(IsCompressedItemAttribute::new, IsCompressedItemAttribute::baseItem);
    public static final IsCompressedItemAttribute.Type TYPE = new IsCompressedItemAttribute.Type(); 

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemCompressionManager.getSequence(world.getRecipeManager(), stack).map(IItemCompressionSequence::getBaseItem)
            .filter(s -> ItemStack.isSameItemSameComponents(stack, baseItem()))
            .isPresent();
    };

    @Override
    public ItemAttributeType getType() {
        return PetrolparkItemAttributeTypes.IS_COMPRESSED;
    };

    @Override
    public String getTranslationKey() {
        return "is_compressed";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{baseItem().getHoverName()};
    };

    public static final class Type implements ItemAttributeType {

        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new IsCompressedItemAttribute(ItemStack.EMPTY);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            return ItemCompressionManager.getSequence(level.getRecipeManager(), stack).stream()
                .map(IItemCompressionSequence::getBaseItem)
                .<ItemAttribute>map(IsCompressedItemAttribute::new)
                .toList();
        };

        @Override
        public MapCodec<IsCompressedItemAttribute> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IsCompressedItemAttribute> streamCodec() {
            return STREAM_CODEC;
        };

    };
    
};
