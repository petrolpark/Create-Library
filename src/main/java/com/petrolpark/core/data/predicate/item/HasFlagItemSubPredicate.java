package com.petrolpark.core.data.predicate.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.util.CodecHelper;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public record HasFlagItemSubPredicate(Holder<Flag> flag) implements ItemSubPredicate {

    public static final Codec<HasFlagItemSubPredicate> CODEC = CodecHelper.singleField(Flag.CODEC, "flag", HasFlagItemSubPredicate::flag, HasFlagItemSubPredicate::new);

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        return ItemFlagPole.get(stack).has(flag);
    };
    
};
