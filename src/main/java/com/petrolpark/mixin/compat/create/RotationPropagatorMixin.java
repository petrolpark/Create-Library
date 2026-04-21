package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {
  
    @Inject(
        method = "getConnectedNeighbors",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;findConnectedNeighbor(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void petrolpark$addMultiPartKineticBlocks(KineticBlockEntity be, CallbackInfoReturnable<List<KineticBlockEntity>> cir, List<KineticBlockEntity> neighbors, BlockPos neighborPos) {
        CompositeKineticBlockEntity.addMultiParts(be, neighborPos, neighbors::add);
    };
};
