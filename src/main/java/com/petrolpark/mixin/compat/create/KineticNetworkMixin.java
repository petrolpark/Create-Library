package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity;
import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity.CompositeKineticBlockEntityPart;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(KineticNetwork.class)
public class KineticNetworkMixin {
    
    @WrapOperation(
        method = "calculateStress",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    public BlockEntity petrolpark$checkCompositeKBEsMatchPos(Level level, BlockPos pos, Operation<BlockEntity> original, @Local KineticBlockEntity be) {
        if (be instanceof CompositeKineticBlockEntityPart part) return original.call(level, pos) instanceof CompositeKineticBlockEntity composite && part.getIndex() >= 0 && part.getIndex() < composite.getParts().size() ? composite.getParts().get(part.getIndex()) : null;
        return original.call(level, pos);
    };

    @WrapOperation(
        method = "calculateCapacity",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    public BlockEntity petrolpark$checkCompositeGeneratingKBEsMatchPos(Level level, BlockPos pos, Operation<BlockEntity> original, @Local KineticBlockEntity be) {
        if (be instanceof CompositeKineticBlockEntityPart part) return original.call(level, pos) instanceof CompositeKineticBlockEntity composite && part.getIndex() >= 0 && part.getIndex() < composite.getParts().size() ? composite.getParts().get(part.getIndex()) : null;
        return original.call(level, pos);
    };
};
