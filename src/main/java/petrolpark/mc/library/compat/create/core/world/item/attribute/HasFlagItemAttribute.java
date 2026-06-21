package petrolpark.mc.library.compat.create.core.world.item.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.registry.PetrolparkItemAttributeTypes;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@RequiresCreate
public record HasFlagItemAttribute(Holder<Flag> flagHolder) implements PetrolparkItemAttribute {

    public static final MapCodec<HasFlagItemAttribute> CODEC = Flag.CODEC
		.xmap(HasFlagItemAttribute::new, HasFlagItemAttribute::flagHolder)
		.fieldOf("value");

	public static final StreamCodec<RegistryFriendlyByteBuf, HasFlagItemAttribute> STREAM_CODEC = CatnipStreamCodecBuilders.nullable(ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.FLAG))
		.map(HasFlagItemAttribute::new, HasFlagItemAttribute::flagHolder);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemFlagPole.get(stack).has(flagHolder);
    };

    @Override
    public ItemAttributeType getType() {
        return PetrolparkItemAttributeTypes.HAS_FLAG;
    };

    @Override
    public String getTranslationKey() {
        return "has_flag";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{Flag.getName(flagHolder)};
    };

    public static class Type implements ItemAttributeType {

        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new HasFlagItemAttribute(null);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            IFlagPole<?, ?> flags = ItemFlagPole.get(stack);
            List<ItemAttribute> list = new ArrayList<>(flags.streamAllFlags()
                .map(HasFlagItemAttribute::new)
                .map(ItemAttribute.class::cast)
                .toList()
            );
            return list;
        };

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return CODEC;
        };

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return STREAM_CODEC;
        };
    };
    
};
