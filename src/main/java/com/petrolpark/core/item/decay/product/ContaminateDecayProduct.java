package com.petrolpark.core.item.decay.product;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDecayProductTypes;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ContaminationLootItemFunction;
import com.petrolpark.core.contamination.ItemContamination;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ContaminateDecayProduct(Holder<Contaminant> contaminantHolder, ContaminationLootItemFunction.Action action) implements IDecayProduct {
    
    public static final MapCodec<ContaminateDecayProduct> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Contaminant.CODEC.fieldOf("contaminant").forGetter(ContaminateDecayProduct::contaminantHolder),
        ContaminationLootItemFunction.Action.CODEC.optionalFieldOf("action", ContaminationLootItemFunction.Action.ADD).forGetter(ContaminateDecayProduct::action)
    ).apply(instance, ContaminateDecayProduct::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContaminateDecayProduct> STREAM_CODEC = StreamCodec.composite(
        Contaminant.STREAM_CODEC, ContaminateDecayProduct::contaminantHolder, 
        ContaminationLootItemFunction.Action.STREAM_CODEC, ContaminateDecayProduct::action,
        ContaminateDecayProduct::new
    );

    @Override
    public ItemStack get(ItemStack stack) {
        action().apply(ItemContamination.get(stack), contaminantHolder);
        return stack;
    };

    @Override
    public DecayProductType getType() {
        return PetrolparkDecayProductTypes.CONTAMINATE.get();
    };

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        return (other instanceof ContaminateDecayProduct cdp && cdp.action() == action() && cdp.contaminantHolder().equals(contaminantHolder()));
    };
};
