package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import petrolpark.mc.library.core.world.fluid.ICustomBlockStateFluid;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

@Mixin(value = OpenEndedPipe.class, remap = false)
public abstract class OpenEndedPipeMixin {

    @Shadow
    private Level world;

    @Shadow
    private BlockPos outputPos;
    
    @Inject(
        method = "provideFluidToSpace(Lnet/neoforged/neoforge/fluids/FluidStack;Z)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void petrolpark$provideCustomBlockStateFluid(FluidStack stack, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        if (
            world != null
            && world.isLoaded(outputPos)
            && world.getBlockState(outputPos).canBeReplaced()
            && stack.getFluid() instanceof ICustomBlockStateFluid customBlockStateFluid
            && AllConfigs.server().fluids.pipesPlaceFluidSourceBlocks.get()
        ) {
            if (simulate || stack.getAmount() == 1000) {
                if (!simulate) world.setBlockAndUpdate(outputPos, customBlockStateFluid.getBlockState());
                cir.setReturnValue(true);
                cir.cancel();
            } else {
                cir.setReturnValue(false);
                cir.cancel();
            }
        };
    };
};
