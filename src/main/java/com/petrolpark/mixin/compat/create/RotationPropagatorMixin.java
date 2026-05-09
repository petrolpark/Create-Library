package com.petrolpark.mixin.compat.create;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity;
import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity.CompositeKineticBlockEntityPart;
import com.petrolpark.compat.create.core.block.entity.IKineticBlockEntityDuck;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {

    @Shadow
    private static void propagateMissingSource(KineticBlockEntity updateTE) {
        throw new AssertionError();
    };
  
    @Inject(
        method = "getConnectedNeighbours",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/RotationPropagator;findConnectedNeighbour(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void petrolpark$addCompositeKineticBlocks(KineticBlockEntity be, CallbackInfoReturnable<List<KineticBlockEntity>> cir, List<KineticBlockEntity> neighbors, Iterator<KineticBlockEntity> iterator, BlockPos neighbourPos) {
        CompositeKineticBlockEntity.addMultiParts(be, neighbourPos, neighbors::add);
    };

    @Inject(
        method = "handleRemoved",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void petrolpark$handleRemovedCompositeKineticBlocks(Level worldIn, BlockPos pos, KineticBlockEntity removedBE, CallbackInfo ci, Iterator<BlockPos> iterator, BlockPos neighbourPos, BlockState neighbourState) {
        if (worldIn.getBlockEntity(neighbourPos) instanceof CompositeKineticBlockEntity be) {
            for (CompositeKineticBlockEntityPart part : be.getParts()) {
                if (part.hasSource() && part.source.equals(pos)) propagateMissingSource(part);
            };
        };
    };

    /**
     * To handle removing {@link CompositeKineticBlockEntity CompositeKineticBlockEntities} correctly, we need to track the index of an individual part among all {@link CompositeKineticBlockEntity#getParts() parts} correctly.
     * To do this, we create an {@code indexFrontier} analagous to {@code frontier} for the positions.
     */
    @Inject(
        method = "propagateMissingSource",
        at = @At("HEAD")
    )
    @SuppressWarnings("null")
    private static void petrolpark$storeMissingSourceIndex(KineticBlockEntity updateTE, CallbackInfo ci, @Share("indexFrontier") LocalRef<List<Integer>> indexFrontierRef, @Share("missingSourceIndex") LocalIntRef missingSourceIndexRef) {
        
        indexFrontierRef.set(new LinkedList<>());
        indexFrontierRef.get().add(updateTE instanceof CompositeKineticBlockEntityPart part ? part.getIndex() : -1); 
        
        final Integer sourceIndexBoxed = ((IKineticBlockEntityDuck)updateTE).getSourceIndex();
        final int index = (
            sourceIndexBoxed != null &&
            sourceIndexBoxed >= 0 &&
            updateTE.hasSource() &&
            updateTE.getLevel().getBlockEntity(updateTE.source) instanceof CompositeKineticBlockEntity composite &&
            sourceIndexBoxed < composite.getParts().size()
        )
            ? sourceIndexBoxed
            : -1;

        missingSourceIndexRef.set(index);
    };

    /**
     * Store the {@code index} of the current {@link CompositeKineticBlockEntityPart}, or {@code -1} if it is not a part.
     */
    @Inject(
        method = "propagateMissingSource",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    private static void petrolpark$getCurrentBEIndex(KineticBlockEntity updateTE, CallbackInfo ci, @Share("index") LocalIntRef indexRef, @Share("indexFrontier") LocalRef<List<Integer>> indexFrontierRef) {
        indexRef.set(indexFrontierRef.get().remove(0));
    };

    /**
     * There are multiple {@link CompositeKineticBlockEntityPart}s in one block - get the right one according to the next entry in {@code indexFrontier}.
     */
    @ModifyExpressionValue(
        method = "propagateMissingSource",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    private static BlockEntity petrolpark$getCorrectCompositePart(BlockEntity original, @Share("index") LocalIntRef indexRef) {
        if (!(original instanceof CompositeKineticBlockEntity composite)) return original;
        int index = indexRef.get();
        if (index < 0 || index >= composite.getParts().size()) return original;
        return composite.getParts().get(index);
    };

    /**
     * The {@link KineticBlockEntity} which was just removed is skipped over when considering potential new sources.
     * We don't want to skip other {@link CompositeKineticBlockEntityPart}s in the same block, so compare their indices and not just positions.
     */
    @ModifyExpressionValue(
        method = "propagateMissingSource",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;equals(Ljava/lang/Object;)Z",
            ordinal = 0
        )
    )
    private static boolean petrolpark$matchesMissingSourceIndex(boolean original, @Local(ordinal = 2) KineticBlockEntity neighbourBE, @Share("missingSourceIndex") LocalIntRef missingSourceIndexRef) {
        return original && (!(neighbourBE instanceof CompositeKineticBlockEntityPart part) || part.getIndex() == missingSourceIndexRef.get());
    };

    /**
     * @see RotationPropagatorMixin#petrolpark$matchesMissingSourceIndex(boolean, KineticBlockEntity, LocalIntRef)
     */
    @ModifyExpressionValue(
        method = "propagateMissingSource",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;equals(Ljava/lang/Object;)Z",
            ordinal = 1
        )
    )
    private static boolean petrolpark$matchesCurrentIndex(boolean original, @Local(ordinal = 2) KineticBlockEntity neighbourBE, @Share("index") LocalIntRef indexRef) {
        final Integer neighbourSourceIndexBoxed = ((IKineticBlockEntityDuck)neighbourBE).getSourceIndex();
        return original && (neighbourSourceIndexBoxed == null ? -1 : neighbourSourceIndexBoxed) == indexRef.get();
    };

    /**
     * Add the newest {@code index} (or {@code -1} if not a {@link CompositeKineticBlockEntityPart}) to the {@code indexFrontier}
     */
    @Inject(
        method = "propagateMissingSource",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 3
        )
    )
    private static void petrolpark$addNeighbourIndexToFrontier(KineticBlockEntity updateTE, CallbackInfo ci, @Share("indexFrontier") LocalRef<List<Integer>> indexFrontierRef, @Local(ordinal = 2) KineticBlockEntity neighbourBE) {
        indexFrontierRef.get().add(neighbourBE instanceof CompositeKineticBlockEntityPart part ? part.getIndex() : -1);
    };

    @ModifyExpressionValue(
        method = "propagateNewSource",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;equals(Ljava/lang/Object;)Z"
        )
    )
    private static boolean petrolpark$checkNeighbourSourceIndex(boolean original, KineticBlockEntity currentBE, @Local(ordinal = 1) KineticBlockEntity neighbourBE) {
        return original && Objects.equals(((IKineticBlockEntityDuck)currentBE).getSourceIndex(), neighbourBE instanceof CompositeKineticBlockEntityPart part ? part.getIndex() : null);
    };

    @WrapOperation(
        method = "propagateNewSource",
        at = @At(
            value = "INVOKE",
            target = "setSource"
        )
    )
    private static void petrolpark$setSourceIndex(KineticBlockEntity kbe, BlockPos source, Operation<Void> original, KineticBlockEntity currentBE, @Local(ordinal = 1) KineticBlockEntity neighbourBE) {
        final KineticBlockEntity sourceBE = kbe == currentBE ? neighbourBE : currentBE;
        ((IKineticBlockEntityDuck)kbe).setSourceIndex((sourceBE instanceof CompositeKineticBlockEntityPart part) ? part.getIndex() : null);
        original.call(kbe, source);
    };
};
