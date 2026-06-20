package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.petrolpark.compat.create.core.world.block.chainConveyor.ChainConveyorItemEvent;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(ChainPackageInteractionPacket.class)
public abstract class ChainPackageInteractionPacketMixin {

    @Shadow
    private BlockPos selectedConnection;
    @Shadow
    private float chainPosition;
    @Shadow
    private boolean removingPackage;
    
    @ModifyExpressionValue(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainPackageInteractionPacket;applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"
        )
    )
    protected ItemStack petrolpark$removeProperly(ItemStack stack, ServerPlayer player, ChainConveyorBlockEntity ccbe) {
        return removingPackage ? ChainConveyorItemEvent.getRemoved(player.level(), stack, ccbe, selectedConnection, chainPosition, false).getStack() : ChainConveyorItemEvent.getAdded(player.level(), stack.copyWithCount(1), ccbe, selectedConnection, chainPosition, false).getStack();
    };
};
