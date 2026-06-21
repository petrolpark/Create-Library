package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
  
    @WrapWithCondition(
        method = "tickPassenger",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;setOldPosAndRot()V"
        )
    )
    public boolean petrolpark$swingLegsIfRidingHorseMillContraption(Entity entity) {
        return !(entity.getVehicle() instanceof HorseMillContraptionEntity);
    };
};
