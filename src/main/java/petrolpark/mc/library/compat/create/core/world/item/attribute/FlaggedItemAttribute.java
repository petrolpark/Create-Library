package petrolpark.mc.library.compat.create.core.world.item.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.registry.PetrolparkItemAttributeTypes;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@RequiresCreate
public record FlaggedItemAttribute(Holder<Flag> flag) implements PetrolparkItemAttribute {

    public static final MapCodec<FlaggedItemAttribute> CODEC = Flag.CODEC
		.xmap(FlaggedItemAttribute::new, FlaggedItemAttribute::flag)
		.fieldOf("value");

	public static final StreamCodec<RegistryFriendlyByteBuf, FlaggedItemAttribute> STREAM_CODEC = CatnipStreamCodecBuilders.nullable(ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.FLAG))
		.map(FlaggedItemAttribute::new, FlaggedItemAttribute::flag);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemFlagPole.get(stack).has(flag);
    };

    @Override
    public FlaggedItemAttribute.Type getType() {
        return PetrolparkItemAttributeTypes.FLAGGED.get();
    };

    @Override
    public String getTranslationKey() {
        return "flagged";
    };

    @Override
    public MutableComponent format(boolean inverted) {
        return inverted ? Flag.getNameColored(flag()).copy() : Flag.getAbsentNameColored(flag()).copy();
    };

    public static class Type implements ItemAttributeType {

        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new FlaggedItemAttribute(null);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            IFlagPole<?, ?> flags = ItemFlagPole.get(stack);
            List<ItemAttribute> list = new ArrayList<>(flags.streamAllFlags()
                .map(FlaggedItemAttribute::new)
                .map(ItemAttribute.class::cast)
                .toList()
            );
            return list;
        };

        @Override
        public MapCodec<FlaggedItemAttribute> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlaggedItemAttribute> streamCodec() {
            return STREAM_CODEC;
        };
    };
    
};
