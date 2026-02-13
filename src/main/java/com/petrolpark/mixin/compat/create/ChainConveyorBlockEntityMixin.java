package com.petrolpark.mixin.compat.create;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorArmInteractionPoint;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorItemEvent;
import com.petrolpark.compat.create.core.chainconveyor.IChainConveyorBlockEntityDuck;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.box.PackageItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(ChainConveyorBlockEntity.class)
public abstract class ChainConveyorBlockEntityMixin extends KineticBlockEntity implements IChainConveyorBlockEntityDuck {

    @Shadow
    List<ChainConveyorPackage> loopingPackages;
    @Shadow
    Map<BlockPos, List<ChainConveyorPackage>> travellingPackages;
    
    public ChainConveyorBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;notifyPortToAnticipate(Lnet/minecraft/core/BlockPos;)V"
        )
    )
    @SuppressWarnings("null")
    public void wrapNotifyPortToAnticipiate(ChainConveyorBlockEntity ccbe, BlockPos offset, Operation<Void> original, @Local ChainConveyorPackage box, @Local ChainConveyorBlockEntity.ConnectedPort port) {
        original.call(ccbe, offset);
        if (ChainConveyorArmInteractionPoint.isEnabled()) level.getBlockEntity(getBlockPos().offset(offset), AllBlockEntityTypes.MECHANICAL_ARM.get()).ifPresent(arm -> ChainConveyorArmInteractionPoint.notifyArmToAnticipate(ccbe, port, arm, box));
    };

    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;exportToPort(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    @SuppressWarnings("null")
    private boolean wrapExportToPort(ChainConveyorBlockEntity ccbe, ChainConveyorPackage box, BlockPos offset, Operation<Boolean> original, @Local ChainConveyorBlockEntity.ConnectedPort port) {
        return original.call(ccbe, box, offset) || (ChainConveyorArmInteractionPoint.isEnabled() && level.getBlockEntity(getBlockPos().offset(offset), AllBlockEntityTypes.MECHANICAL_ARM.get()).map(arm -> ChainConveyorArmInteractionPoint.exportToArm(ccbe, port, arm, box)).orElse(false));
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;drop(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;)V",
        at = @At("HEAD")
    )
    @SuppressWarnings("null")
    private void inDrop(ChainConveyorPackage box, CallbackInfo ci) {
        if (!(box.item.getItem() instanceof PackageItem)) {
            final Vec3 pos = box.worldPosition.subtract(0d, 0.5d, 0d);
            level.addFreshEntity(new ItemEntity(level, pos.x(), pos.y(), pos.z(), ChainConveyorItemEvent.getRemoved(level, (ChainConveyorBlockEntity)(Object)this, null, box, false).getStack()));
            ci.cancel();
        };
    };

    @Override
    public List<ChainConveyorPackage> getLoopingPackages() {
        return loopingPackages;
    };

    @Override
    public Map<BlockPos, List<ChainConveyorPackage>> getTravellingPackages() {
        return travellingPackages;
    };
};
