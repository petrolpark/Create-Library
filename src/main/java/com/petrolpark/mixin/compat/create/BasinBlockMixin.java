package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.block.entity.basin.BelowBasinOperatingBlockEntity;
import com.petrolpark.compat.create.core.block.entity.basin.DirectlyAboveBasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BasinBlock.class)
public abstract class BasinBlockMixin extends Block {

    public BasinBlockMixin(Properties properties) {
        super(properties);
        throw new AssertionError();
    };

    @WrapMethod(
        method = "Lcom/simibubi/create/content/processing/basin/BasinBlock;canSurvive(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z",
        remap = false
    )
    public boolean petrolpark$checkOtherOperators(BlockState state, LevelReader world, BlockPos pos, Operation<Boolean> operation) {
        return operation.call(state, world, pos) || world.getBlockEntity(pos.above()) instanceof DirectlyAboveBasinOperatingBlockEntity || world.getBlockEntity(pos.below()) instanceof BelowBasinOperatingBlockEntity;
    };
    
};
