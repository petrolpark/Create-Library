package petrolpark.mc.library.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import petrolpark.mc.library.compat.create.core.world.block.chainConveyor.ChainConveyorArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

@Mixin(ArmInteractionPointHandler.class)
public class ArmInteractionPointHandlerMixin {
    
    @WrapOperation(
        method = "Lcom/simibubi/create/content/kinetics/mechanicalArm/ArmInteractionPointHandler;flushSettings(Lnet/minecraft/core/BlockPos;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"
        )
    )
    private static boolean wrapCloserThan(BlockPos thisPos, Vec3i pos, double distance, Operation<Boolean> original, @Local ArmInteractionPoint point) {
        return point instanceof ChainConveyorArmInteractionPoint chainPoint ? chainPoint.inRange(pos) : original.call(thisPos, pos, distance);
    };
};
