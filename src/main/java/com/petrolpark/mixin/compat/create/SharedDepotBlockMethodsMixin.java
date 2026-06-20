package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.petrolpark.compat.create.core.world.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.world.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;

/**
 * Handle the manual addition of {@link DirectionalTransportedItemStack}s to Depots by Players.
 */
@Mixin(SharedDepotBlockMethods.class)
public class SharedDepotBlockMethodsMixin {

    @ModifyArg(
        method = "onUse(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;setHeldItem(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)V"
        ),
        index = 0,
        remap = false
    )
    private static TransportedItemStack petrolpark$makeDirectionalItem(TransportedItemStack transported) {
        if (transported.stack.getItem() instanceof IDirectionalOnBelt directionalItem) return directionalItem.makeDirectionalTransportedItemStack(transported);
        return transported;
    };
};
