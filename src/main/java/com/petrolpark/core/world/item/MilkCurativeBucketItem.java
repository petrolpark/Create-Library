package com.petrolpark.core.world.item;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.EffectCures;

public class MilkCurativeBucketItem extends DrinkableBucketItem {

    public MilkCurativeBucketItem(Fluid content, Item.Properties properties) {
        super(content, properties);
    };

    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        if (!level.isClientSide()) entityLiving.removeEffectsCuredBy(EffectCures.MILK);
        return super.finishUsingItem(stack, level, entityLiving);
    };
    
};
