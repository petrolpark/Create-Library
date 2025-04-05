package com.petrolpark.compat.create.core.item;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.PetrolparkItemAttributeTypes;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
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
public record HasContaminantItemAttribute(Holder<Contaminant> contaminantHolder) implements ItemAttribute {

    public static final MapCodec<HasContaminantItemAttribute> CODEC = Contaminant.CODEC
		.xmap(HasContaminantItemAttribute::new, HasContaminantItemAttribute::contaminantHolder)
		.fieldOf("value");

	public static final StreamCodec<RegistryFriendlyByteBuf, HasContaminantItemAttribute> STREAM_CODEC = CatnipStreamCodecBuilders.nullable(ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.CONTAMINANT))
		.map(HasContaminantItemAttribute::new, HasContaminantItemAttribute::contaminantHolder);

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemContamination.get(stack).has(contaminantHolder);
    };

    @Override
    public ItemAttributeType getType() {
        return PetrolparkItemAttributeTypes.HAS_CONTAMINANT;
    };

    @Override
    public String getTranslationKey() {
        return "has_contaminant";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{Contaminant.getName(contaminantHolder)};
    };

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new HasContaminantItemAttribute(null);
        };

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            IContamination<?, ?> contamination = ItemContamination.get(stack);
            List<ItemAttribute> list = new ArrayList<>(contamination.streamAllContaminants()
                .map(HasContaminantItemAttribute::new)
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
