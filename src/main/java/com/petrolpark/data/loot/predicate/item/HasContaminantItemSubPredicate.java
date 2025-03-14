package com.petrolpark.data.loot.predicate.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public record HasContaminantItemSubPredicate(Holder<Contaminant> contaminant) implements ItemSubPredicate {

    public static final MapCodec<HasContaminantItemSubPredicate> CODEC = NetworkHelper.singleFieldMapCodec(Contaminant.CODEC, "contaminant", HasContaminantItemSubPredicate::contaminant, HasContaminantItemSubPredicate::new);

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        return ItemContamination.get(stack).has(contaminant.value());
    };
    
};
