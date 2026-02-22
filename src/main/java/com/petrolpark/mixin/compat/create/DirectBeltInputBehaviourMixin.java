package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@Mixin(DirectBeltInputBehaviour.class)
public class DirectBeltInputBehaviourMixin {
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/belt/behaviour/DirectBeltInputBehaviour;handleInsertion(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;",
        remap = false
    )
    public ItemStack petrolpark$insertDirectional(TransportedItemStack stack, Direction side, boolean simulate, Operation<ItemStack> original) {
        if (!(stack instanceof DirectionalTransportedItemStack) && stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) { // If not already cast to a Directional transported stack
           stack = directionalItem.makeDirectionalTransportedItemStack(stack);
        };
        return original.call(stack, side, simulate);
    };
};
