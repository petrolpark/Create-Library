package com.petrolpark.core.item;

import javax.annotation.Nonnull;

import com.petrolpark.core.world.fluid.FluidContainerItem;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class DrinkableFluidContainerItem extends FluidContainerItem {

    public static final DrinkableFluidContainerItem drinkableBottle(NonNullSupplier<Fluid> fluid, Item.Properties properties) {
        return new DrinkableFluidContainerItem(fluid, 250, Items.GLASS_BOTTLE, properties);
    };

    public DrinkableFluidContainerItem(NonNullSupplier<Fluid> fluid, int containerVolume, ItemLike emptyContainer, Item.Properties properties) {
        super(fluid, containerVolume, emptyContainer, properties);
    };

    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        super.finishUsingItem(stack, level, entityLiving);
        if (entityLiving instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        };

        if (stack.isEmpty()) {
            return new ItemStack(emptyContainer);
        } else {
            if (entityLiving instanceof Player player && !player.hasInfiniteMaterials()) {
                ItemStack itemstack = new ItemStack(emptyContainer);
                if (!player.getInventory().add(itemstack)) {
                    player.drop(itemstack, false);
                };
            };

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
    };
    
};
