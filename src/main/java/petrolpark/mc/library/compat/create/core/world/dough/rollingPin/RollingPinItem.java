package petrolpark.mc.library.compat.create.core.world.dough.rollingPin;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import petrolpark.mc.library.core.world.item.wooden.WoodenItem;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.util.RayHelper;

@ParametersAreNonnullByDefault
public class RollingPinItem extends WoodenItem implements ISharedFeature {

    public RollingPinItem(Item.Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Player player = context.getPlayer();
        if (player != null && canBeRolled(context)) {
            return ItemUtils.startUsingInstantly(context.getLevel(), player, context.getHand()).getResult();
        };
        return super.useOn(context);
    };

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    };

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        if (!(livingEntity.pick(RayHelper.getBlockReach(livingEntity), 1f, false) instanceof BlockHitResult bhr) || !(livingEntity instanceof Player player)) return;
        final BlockState state = level.getBlockState(bhr.getBlockPos());

        final UseOnContext context = new UseOnContext(player, player.getUsedItemHand(), bhr);

        if (remainingUseDuration == 11) {
            if (state.getBlock() instanceof IRollableBlock rollable) {
                rollable.rollingPinRoll(context);
            } else {
                final BlockState flattened = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
                if (flattened != null) {
                    level.setBlockAndUpdate(bhr.getBlockPos(), flattened);
                    level.playSound(player, bhr.getBlockPos(), SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1f, 1f);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, bhr.getBlockPos(), GameEvent.Context.of(player, flattened));
                };
            };
        } else if (remainingUseDuration == 1 && canBeRolled(context)) { // Try continuing flattening
            ItemUtils.startUsingInstantly(level, player, player.getUsedItemHand());
            player.useItemRemaining = 18;
        };
    };

    public static final boolean canBeRolled(UseOnContext context) {
        final BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof IRollableBlock rollable && rollable.canBeRollingPinRolled(context)) return true;
        final BlockState flattened = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, true);
        return flattened != null && !flattened.equals(state);
    };

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    };

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SHOVEL_FLATTEN;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.ROLLING_PIN;
    };
    
};
