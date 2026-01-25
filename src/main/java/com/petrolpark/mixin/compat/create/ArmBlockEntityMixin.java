package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorArmInteractionPoint;
import com.petrolpark.compat.create.core.chainconveyor.IArmBlockEntityDuck;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;

import net.minecraft.world.item.ItemStack;

@Mixin(ArmBlockEntity.class)
public abstract class ArmBlockEntityMixin implements IArmBlockEntityDuck {

    @Shadow
    List<ArmInteractionPoint> inputs;
    @Shadow
    List<ArmInteractionPoint> outputs;
    @Shadow
    float chasedPointProgress;
    @Shadow
    int chasedPointIndex;
    @Shadow
    ItemStack heldItem;
    @Shadow
    ArmBlockEntity.Phase phase;
    @Shadow
    abstract ArmInteractionPoint getTargetedInteractionPoint();
    @Shadow
    abstract void searchForItem();
    @Shadow
    abstract void searchForDestination();
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmBlockEntity;initInteractionPoints()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;clear()V",
            ordinal = 0
        )
    )
    protected void inInitInteractionPointsHead(CallbackInfo ci) {
        ChainConveyorArmInteractionPoint.deregisterAll((ArmBlockEntity)(Object)this);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmBlockEntity;initInteractionPoints()V",
        at = @At("RETURN")
    )
    protected void inInitInteractionPointsTail(CallbackInfo ci) {
        ChainConveyorArmInteractionPoint.registerAll((ArmBlockEntity)(Object)this);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmBlockEntity;destroy()V",
        at = @At("HEAD")
    )
    public void inDestroy(CallbackInfo ci) {
        ChainConveyorArmInteractionPoint.deregisterAll((ArmBlockEntity)(Object)this);
    }

    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmBlockEntity;collectItem()V"
    )
    public void wrapCollectItem(Operation<Void> original) {
        if (getTargetedInteractionPoint() instanceof ChainConveyorArmInteractionPoint chainPoint && chainPoint.isValid()) return; //TODO check if Item got removed from chain in mean time?
        original.call();
    };

    @Override
    public List<ArmInteractionPoint> getInputs() {
        return inputs;
    };

    @Override
    public List<ArmInteractionPoint> getOutputs() {
        return outputs;
    };

    @Override
    public float getChasedPointProgress() {
        return chasedPointProgress;
    };

    @Override
    public void setChasedPointProgress(float progress) {
        chasedPointProgress = progress;
    };

    @Override
    public void setChasedPointIndex(int index) {
        chasedPointIndex = index;
    };

    @Override
    public ItemStack getHeldItem() {
        return heldItem;
    };

    @Override
    public void setHeldItem(ItemStack item) {
        this.heldItem = item;
    };

    @Override
    public ArmBlockEntity.Phase getPhase() {
        return phase;
    };

    @Override
    public void setPhase(ArmBlockEntity.Phase phase) {
        this.phase = phase;
    };

    @Override
    public ArmInteractionPoint invokeGetTargetedInteractionPoint() {
        return getTargetedInteractionPoint();
    };

    @Override
    public void invokeSearchForItem() {
        searchForItem();
    };

    @Override
    public void invokeSearchForDestination() {
        searchForDestination();
    };
};
