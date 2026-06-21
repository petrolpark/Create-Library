package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@Mixin(value = FluidTank.class, remap = false)
public abstract class FluidTankMixin implements IFluidHandler, IFluidTank {

    // @Shadow
    // public abstract void setFluid(FluidStack stack);
    
    // @Inject(
    //     method = "fill(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)I",
    //     at = @At("RETURN"),
    //     cancellable = true,
    //     remap = false
    // )
    // @SuppressWarnings("overwrite")
    // public void inFill(FluidStack resource, FluidAction action, CallbackInfoReturnable<Integer> cir) {
    //     if (cir.getReturnValueI() == 0) cir.setReturnValue(FluidHelper.fillTankWithMixer((FluidTank)(Object)this, resource, action));
    // };
};
