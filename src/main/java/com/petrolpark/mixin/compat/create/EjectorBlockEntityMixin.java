package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.compat.create.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.item.directional.IDirectionalOnBelt;
import com.petrolpark.mixin.compat.create.accessor.DepotBehaviourAccessor;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@Mixin(value = EjectorBlockEntity.class, remap = false)
public abstract class EjectorBlockEntityMixin {
    
    @Inject(
        method = "launchItems()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/depot/EjectorBlockEntity;addToLaunchedItems(Lnet/minecraft/world/item/ItemStack;)Z",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inLaunchItemHeld(CallbackInfo ci, ItemStack heldItemStack, Direction funnelFacing) {
        TransportedItemStack stack = ((DepotBehaviourAccessor)getDepotBehaviour()).getHeldItem();
        if (stack instanceof DirectionalTransportedItemStack directionalStack && directionalStack.stack.getItem() instanceof IDirectionalOnBelt item) item.launch(directionalStack, funnelFacing.getOpposite());
    };

    @Accessor("depotBehaviour")
    public abstract DepotBehaviour getDepotBehaviour();
};
