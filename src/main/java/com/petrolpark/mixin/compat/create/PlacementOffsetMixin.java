package com.petrolpark.mixin.compat.create;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.create.core.block.IReplaceableBlock;

import net.createmod.catnip.placement.PlacementOffset;
import net.createmod.catnip.platform.services.ModHooksHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PlacementOffset.class)
public abstract class PlacementOffsetMixin {

    @Shadow
    private Vec3i pos;

    @Shadow
    private Function<BlockState, BlockState> stateTransform;

    @Shadow
    public abstract boolean isReplaceable(Level world);
    
    @ModifyExpressionValue(
        method = "placeInWorld",
        at = @At(
            value = "INVOKE",
            target = "isReplaceable",
            ordinal = 0
        )
    )
    public boolean petrolpark$deferReplaceableCheck(boolean replaceable) {
        return true;
    };

    @WrapOperation(
        method = "placeInWorld",
        at = @At( 
            value = "INVOKE",
            target = "playerPlaceSingleBlock"
        )
    )
    public boolean petrolpark$allowReplacingBlocks(ModHooksHelper hooks, Player player, Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (isReplaceable(level)) return original.call(hooks, player, level, pos, state); // This is the check we deferred
        final BlockState existingState = level.getBlockState(pos);
        if (existingState.getBlock() instanceof IReplaceableBlock replaceable) {
            final BlockState replacedState = replaceable.getReplacedState(level, pos, existingState, state, player);
            if (replacedState != null) return original.call(hooks, player, level, pos, replacedState);
        };
        if (state.getBlock() instanceof IReplaceableBlock replaceable) {
            final BlockState replacedState = replaceable.getReplacedState(level, pos, existingState, state, player);
            if (replacedState != null) return original.call(hooks, player, level, pos, replacedState);
        };
        return true;
    };
};
