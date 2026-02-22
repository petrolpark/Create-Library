package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;

import net.minecraft.world.item.ItemStack;

@Mixin(value = DepotBehaviour.class, remap = false)
public abstract class DepotBehaviourMixin {
    
    @WrapMethod(
        method = "insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
        remap = false
    )
    public ItemStack petrolpark$insertDirectional(TransportedItemStack heldItem, boolean simulate, Operation<ItemStack> original) {
        if (!(heldItem instanceof DirectionalTransportedItemStack) && heldItem.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            heldItem = directionalItem.makeDirectionalTransportedItemStack(heldItem);
        };
        return original.call(heldItem, simulate);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;tick(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)Z",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void petrolpark$refreshDirectionalStackAngle(TransportedItemStack heldItem, CallbackInfoReturnable<Boolean> ci, float diff) {
        if (heldItem instanceof DirectionalTransportedItemStack directionalStack) directionalStack.refreshAngle();
    };

    @Accessor("heldItem")
    public abstract TransportedItemStack getHeldItem();
};
