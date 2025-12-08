package com.petrolpark.core.world.block;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * A Block which can be instantly picked up and will always remove the Item when placed, even in Creative.
 */
@EventBusSubscriber
public interface IPickUpPutDownBlock {

    /**
     * This should be called in the {@link net.minecraft.world.item.BlockItem#place(BlockPlaceContext) place} method of the corresponding BlockItem.
     * @param context
     * @param result Usually just the super result of placing
     */
    public static InteractionResult removeItemFromInventory(BlockPlaceContext context, InteractionResult result) {
        final Player player = context.getPlayer();
        if (result == InteractionResult.sidedSuccess(context.getLevel().isClientSide()) && player != null && player.hasInfiniteMaterials()) {
            context.getItemInHand().shrink(1); // Remove the Item from the Inventory even if in Creative
        };
        return result;
    };

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        final BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.getBlock() instanceof IPickUpPutDownBlock) {
            if (!(event.getEntity() instanceof FakePlayer) && event.getLevel() instanceof ServerLevel level) {
                final List<ItemStack> drops = Block.getDrops(state, level, event.getPos(), level.getBlockEntity(event.getPos()), event.getEntity(), event.getItemStack());
                final BlockState removedBlockState = state.getBlock().playerWillDestroy(level, event.getPos(), state, event.getEntity());
                final FluidState fluidState = level.getFluidState(event.getPos());
                if (state.getBlock().onDestroyedByPlayer(state, level, event.getPos(), event.getEntity(), false, fluidState)) {
                    state.getBlock().destroy(level, event.getPos(), removedBlockState);
                    event.getEntity().awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
                    drops.forEach(event.getEntity().getInventory()::placeItemBackInInventory);
                };
            };
            event.setUseBlock(TriState.FALSE);
            event.setCanceled(true);
        };
    };
    
};
