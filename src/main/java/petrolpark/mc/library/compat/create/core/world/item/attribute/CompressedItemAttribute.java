package petrolpark.mc.library.compat.create.core.world.item.attribute;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.compat.create.registry.PetrolparkItemAttributeTypes;
import petrolpark.mc.library.core.world.item.compression.IItemCompressionSequence;
import petrolpark.mc.library.core.world.item.compression.ItemCompressionManager;
import petrolpark.mc.library.util.codec.CodecHelper;

public record CompressedItemAttribute(ItemStack baseItem) implements PetrolparkItemAttribute {

    public static final MapCodec<CompressedItemAttribute> CODEC = CodecHelper.singleFieldMap(ItemStack.SINGLE_ITEM_CODEC, "base_item", CompressedItemAttribute::baseItem, CompressedItemAttribute::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressedItemAttribute> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(CompressedItemAttribute::new, CompressedItemAttribute::baseItem);
    public static final CompressedItemAttribute.Type TYPE = new CompressedItemAttribute.Type(); 

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemCompressionManager.getSequence(world.getRecipeManager(), stack).map(IItemCompressionSequence::getBaseItem)
            .filter(s -> ItemStack.isSameItemSameComponents(stack, baseItem()))
            .isPresent();
    };

    @Override
    public CompressedItemAttribute.Type getType() {
        return PetrolparkItemAttributeTypes.COMPRESSED.get();
    };

    @Override
    public String getTranslationKey() {
        return "compressed";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{baseItem().getHoverName()};
    };

    public static final class Type implements ItemAttributeType {

        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new CompressedItemAttribute(ItemStack.EMPTY);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            return ItemCompressionManager.getSequence(level.getRecipeManager(), stack).stream()
                .map(IItemCompressionSequence::getBaseItem)
                .<ItemAttribute>map(CompressedItemAttribute::new)
                .toList();
        };

        @Override
        public MapCodec<CompressedItemAttribute> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CompressedItemAttribute> streamCodec() {
            return STREAM_CODEC;
        };

    };
    
};
