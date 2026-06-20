package com.petrolpark.compat.create.core.world.dough.rollingPin;

import javax.annotation.Nonnull;

import com.petrolpark.core.world.item.wooden.WoodenItem;
import com.petrolpark.shared.ISharedFeature;
import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.util.RayHelper;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;

public class RollingPinItem extends WoodenItem implements ISharedFeature {

    public RollingPinItem(Item.Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        final Player player = context.getPlayer();
        if (player != null && context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof IRollableBlock rollable && rollable.canBeRollingPinRolled(context.getLevel(), context.getClickedPos(), context.getHorizontalDirection())) {
            return ItemUtils.startUsingInstantly(context.getLevel(), player, context.getHand()).getResult();
        };
        return super.useOn(context);
    };

    @Override
    public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        return 24;
    };

    @Override
    public void onUseTick(@Nonnull Level level, @Nonnull LivingEntity livingEntity, @Nonnull ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        if (livingEntity.pick(RayHelper.getBlockReach(livingEntity), 1f, false) instanceof BlockHitResult bhr && level.getBlockState(bhr.getBlockPos()).getBlock() instanceof IRollableBlock rollable) {
            if (remainingUseDuration == 11) rollable.rollingPinRoll(level, bhr.getBlockPos(), livingEntity.getDirection(), livingEntity instanceof Player && !(livingEntity instanceof FakePlayer));
            if (remainingUseDuration == 1 && rollable.canBeRollingPinRolled(level, bhr.getBlockPos(), livingEntity.getDirection()) && livingEntity instanceof Player player) {
                ItemUtils.startUsingInstantly(level, player, player.getUsedItemHand());
                player.useItemRemaining = 18;
            };
        };
    };

    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.NONE;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.ROLLING_PIN;
    };
    
};
