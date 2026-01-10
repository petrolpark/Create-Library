package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.mixin.compat.create.accessor.client.ArmInteractionPointHandlerAccessor;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;

@Mixin(ChainConveyorInteractionHandler.class)
public class ChainConveyorInteractionHandlerMixin {
    
    @ModifyReturnValue(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorInteractionHandler;isActive()Z",
        at = @At("RETURN")
    )
    private static boolean isActive(boolean original) {
        return original || (PetrolparkConfigs.server().createArmsTargetChainConveyors.get() && ArmInteractionPointHandlerAccessor.getCurrentItem() != null);
    };
};
