package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.compat.create.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;

import net.minecraft.world.item.ItemStack;

@Mixin(value = DepotBehaviour.class, remap = false)
public abstract class DepotBehaviourMixin {
    
    @Inject(
        method = "insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inInsert(TransportedItemStack heldItem, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (!(heldItem instanceof DirectionalTransportedItemStack) && heldItem.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            cir.setReturnValue(((DepotBehaviour)(Object)this).insert(directionalItem.makeDirectionalTransportedItemStack(heldItem), simulate));
            cir.cancel();
        };
    };

    @Inject(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;tick(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)Z",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inTick(TransportedItemStack heldItem, CallbackInfoReturnable<Boolean> ci, float diff) {
        if (heldItem instanceof DirectionalTransportedItemStack directionalStack) directionalStack.refreshAngle();
    };

    @Accessor("heldItem")
    public abstract TransportedItemStack getHeldItem();
};
