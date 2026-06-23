package petrolpark.mc.library.compat.create.core.world.item.attribute;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.compat.create.registry.PetrolparkItemAttributeTypes;
import petrolpark.mc.library.core.world.item.wooden.WoodCraftingShapedRecipe;
import petrolpark.mc.library.util.WoodHelper;
import petrolpark.mc.library.util.WoodHelper.Wood;
import petrolpark.mc.library.util.codec.CodecHelper;

public record WoodItemAttribute(Wood wood) implements PetrolparkItemAttribute {

    public static final MapCodec<WoodItemAttribute> CODEC = CodecHelper.singleFieldMap(Wood.CODEC, "wood", WoodItemAttribute::wood, WoodItemAttribute::new);
    public static final StreamCodec<ByteBuf, WoodItemAttribute> STREAM_CODEC = StreamCodec.composite(Wood.STREAM_CODEC, WoodItemAttribute::wood, WoodItemAttribute::new);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return Objects.equals(WoodCraftingShapedRecipe.getWood(stack), wood());
    };

    @Override
    public WoodItemAttribute.Type getType() {
        return PetrolparkItemAttributeTypes.WOOD.get();
    };

    @Override
    public String getTranslationKey() {
        return "wood";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{WoodHelper.getName(wood())};
    };

    public static class Type implements ItemAttributeType {

        @Override
        public @NotNull WoodItemAttribute createAttribute() {
            return new WoodItemAttribute(WoodHelper.OAK);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            return Optional.ofNullable(WoodCraftingShapedRecipe.getWood(stack)).<ItemAttribute>map(WoodItemAttribute::new).stream().toList();
        };

        @Override
        public MapCodec<WoodItemAttribute> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<ByteBuf, WoodItemAttribute> streamCodec() {
            return STREAM_CODEC;
        };

    };
    
};
