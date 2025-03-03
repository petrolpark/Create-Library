package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.util.FluidHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@Mixin(value = FluidTank.class, remap = false)
public abstract class FluidTankMixin implements IFluidHandler, IFluidTank {

    @Shadow
    public abstract void setFluid(FluidStack stack);
    
    @Inject(
        method = "fill(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)I",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    @SuppressWarnings("overwrite")
    public void inFill(FluidStack resource, FluidAction action, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() == 0) cir.setReturnValue(FluidHelper.fillTankWithMixer((FluidTank)(Object)this, resource, action));
    };
};
