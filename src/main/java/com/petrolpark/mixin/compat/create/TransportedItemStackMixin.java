package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Rotation;

@RequiresCreate
@Mixin(TransportedItemStack.class)
public class TransportedItemStackMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;read(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;",
        at = @At("RETURN"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    private static void inRead(CompoundTag nbt, HolderLookup.Provider registries, CallbackInfoReturnable<TransportedItemStack> cir, TransportedItemStack stack) {
        if (stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            DirectionalTransportedItemStack directionalStack = directionalItem.makeDirectionalTransportedItemStack(stack);
            if (nbt.contains("Rotation", Tag.TAG_INT)) {
                directionalStack.setRotation(Rotation.values()[nbt.getInt("Rotation")]);
            };
            cir.setReturnValue(directionalStack);
            cir.cancel();
        };
    };
};
