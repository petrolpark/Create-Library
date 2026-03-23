package com.petrolpark.core.item;

import javax.annotation.Nonnull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class DrinkableBucketItem extends BucketItem {

    public static final FluidBucketWrapper provideCapability(ItemStack stack, Void v) {
        return new FluidBucketWrapper(stack);
    };

    public DrinkableBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    };

    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        super.finishUsingItem(stack, level, entityLiving); // Eat food
        if (entityLiving instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        };

        if (entityLiving instanceof Player player) {
            return ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET), false);
        } else {
            stack.consume(1, entityLiving);
            return stack;
        }
    };

    @Override
    public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        return 32;
    };

    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    };

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
        // Don't place any Fluid
    };
    
};
