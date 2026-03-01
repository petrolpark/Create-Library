package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;

@Mixin(PressingBehaviour.class)
public class PressingBehaviourMixin {
    
    @Shadow
    public PressingBehaviour.Mode mode; 

    @ModifyReturnValue(
        method = "Lcom/simibubi/create/content/kinetics/press/PressingBehaviour;onBasin()Z",
        at = @At("RETURN")
    )
    public boolean petrolpark$onMeshBasin(boolean original) {
        return original || mode == PressingBehaviour.Mode.valueOf("PETROLPARK_MESH_BASIN");
    };
};
