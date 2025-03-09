package com.petrolpark.contamination;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.PetrolparkItemAttributes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@RequiresCreate
public record HasContaminantItemAttribute(@Nullable Contaminant contaminant) implements ItemAttribute {

    public static final MapCodec<HasContaminantItemAttribute> CODEC = PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.CONTAMINANT).byNameCodec()
		.xmap(HasContaminantItemAttribute::new, HasContaminantItemAttribute::contaminant)
		.fieldOf("value");

	public static final StreamCodec<RegistryFriendlyByteBuf, HasContaminantItemAttribute> STREAM_CODEC = CatnipStreamCodecBuilders.nullable(ByteBufCodecs.registry(PetrolparkRegistries.Keys.CONTAMINANT))
		.map(HasContaminantItemAttribute::new, HasContaminantItemAttribute::contaminant);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemContamination.get(stack).has(contaminant);
    };

    @Override
    public ItemAttributeType getType() {
        return PetrolparkItemAttributes.HAS_CONTAMINANT;
    };

    @Override
    public String getTranslationKey() {
        return "has_contaminant";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{contaminant.getName()};
    };

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new HasContaminantItemAttribute(null);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            IContamination<?, ?> contamination = ItemContamination.get(stack);
            List<ItemAttribute> list = new ArrayList<>(contamination.streamAllContaminants().map(HasContaminantItemAttribute::new).map(ItemAttribute.class::cast).toList());
            IntrinsicContaminants.getShownIfAbsent(contamination).forEach(c -> {list.add(new HasContaminantItemAttribute(c));});
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
