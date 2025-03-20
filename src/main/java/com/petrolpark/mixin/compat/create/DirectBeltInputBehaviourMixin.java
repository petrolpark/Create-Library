package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@Mixin(DirectBeltInputBehaviour.class)
public class DirectBeltInputBehaviourMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/belt/behaviour/DirectBeltInputBehaviour;handleInsertion(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inHandleInsertion(TransportedItemStack stack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (!(stack instanceof DirectionalTransportedItemStack) && stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) { // If not already cast to a Directional transported stack
            cir.setReturnValue(((DirectBeltInputBehaviour)(Object)this).handleInsertion(directionalItem.makeDirectionalTransportedItemStack(stack), side, simulate));
        };
    };
};
