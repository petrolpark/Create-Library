package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.create.PetrolparkCreateBlocks;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.state.BlockState;

@Mixin(CrushingWheelBlock.class)
public class CrushingWheelBlockMixin {
    
    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/crusher/CrushingWheelBlock;updateControllers(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            ordinal = 0
        )
    )
    public boolean petrolpark$allowEncasedControllers(BlockEntry<CrushingWheelBlock> block, BlockState state, Operation<Boolean> original) {
        return original.call(block, state) || PetrolparkCreateBlocks.ENCASED_CRUSHING_WHEEL_CONTROLLER.has(state);
    };
};
