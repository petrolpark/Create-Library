package com.petrolpark.mixin.compat.create;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.create.core.world.block.IReplaceableBlock;

import net.createmod.catnip.placement.PlacementOffset;
import net.createmod.catnip.platform.services.ModHooksHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(PlacementOffset.class)
public abstract class PlacementOffsetMixin {

    @Shadow
    private Vec3i pos;

    @Shadow
    private Function<BlockState, BlockState> stateTransform;
   
    @ModifyExpressionValue(
        method = "placeInWorld",
        at = @At(
            value = "INVOKE",
            target = "isReplaceable",
            ordinal = 0
        )
    )
    public boolean petrolpark$allowReplaceableBlocks(boolean replaceable, Level world, BlockItem blockItem, Player player, InteractionHand hand, BlockHitResult ray) {
        final BlockPos pos = new BlockPos(this.pos);
        final BlockState existingState = world.getBlockState(pos);
        final BlockState stateToPlace = stateTransform.apply(blockItem.getBlock().defaultBlockState());
        return replaceable ||
            (existingState.getBlock() instanceof IReplaceableBlock replaceableBlock && replaceableBlock.canBeReplaced(world, pos, existingState, stateToPlace, player)) ||
            (stateToPlace.getBlock() instanceof IReplaceableBlock replaceableBlock && replaceableBlock.canBeReplaced(world, pos, existingState, stateToPlace, player));
    };

    @WrapOperation(
        method = "placeInWorld",
        at = @At(
            value = "INVOKE",
            target = "playerPlaceSingleBlock"
        )
    )
    public boolean petrolpark$replaceState(ModHooksHelper hooks, Player player, Level level, BlockPos pos, BlockState newState, Operation<Boolean> original) {
        final BlockState existingState = level.getBlockState(pos);
        if (existingState.getBlock() instanceof IReplaceableBlock replaceableBlock) {
            return original.call(hooks,player, level, pos, replaceableBlock.getReplacedState(level, pos, existingState, newState, player));
        } else if (newState.getBlock() instanceof IReplaceableBlock replaceableBlock) {
            return original.call(hooks, player, level, pos, replaceableBlock.getReplacedState(level, pos, existingState, newState, player));
        };
        return original.call(hooks, player, level, pos, newState);
    };
};
