package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;
import com.simibubi.create.content.logistics.box.PackageItem;

@Mixin(ChainConveyorVisual.class)
public class ChainConveyorVisualMixin {
    
    @WrapMethod(
        method = "setupBoxVisual"
    )
    private void wrapSetupBoxVisual(ChainConveyorBlockEntity be, ChainConveyorPackage box, float partialTicks, Operation<Void> original) {
        if (PackageItem.isPackage(box.item)) original.call(be, box, partialTicks);
    };
};
