package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.core.block.composite.CompositeKineticBlockEntity;
import com.petrolpark.compat.create.core.block.entity.behaviour.ContaminationBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity {
    
    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("HEAD"),
        remap = false
    )
    public void petrolpark$addContaminationBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        if (PetrolparkTags.Items.CONTAMINABLE.matches(getBlockState().getBlock().asItem())) behaviours.add(new ContaminationBehaviour(this));
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;validateKinetics()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;removeSource()V",
            ordinal = 1
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    public void petrolpark$allowCompositeBlockEntitiesAsSources(CallbackInfo ci, BlockEntity blockEntity, KineticBlockEntity sourceBE) {
        if (blockEntity instanceof CompositeKineticBlockEntity) ci.cancel();
    };
};
