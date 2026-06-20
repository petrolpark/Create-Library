package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.compat.create.core.world.block.chainConveyor.ChainConveyorArmInteractionPoint;
import com.petrolpark.compat.create.core.world.block.chainConveyor.ChainConveyorItemEvent;
import com.petrolpark.mixin.compat.create.accessor.client.ArmInteractionPointHandlerAccessor;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

@Mixin(ChainConveyorInteractionHandler.class)
public class ChainConveyorInteractionHandlerMixin {
    
    @ModifyReturnValue(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorInteractionHandler;isActive()Z",
        at = @At("RETURN")
    )
    private static boolean isActive(boolean original) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        final LocalPlayer player = mc.player;
        return original 
            || (ChainConveyorArmInteractionPoint.isEnabled() && ArmInteractionPointHandlerAccessor.getCurrentItem() != null)
            || (level != null && player != null && ChainConveyorItemEvent.canAddClient(level, player.getMainHandItem()));
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorInteractionHandler;onUse()Z",
        at = @At("TAIL")
    )
    private static void inOnUse(CallbackInfoReturnable<Boolean> cir) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        final LocalPlayer player = mc.player;
        if (level == null || player == null) return;
        if (ChainConveyorItemEvent.canAddClient(level, player.getMainHandItem())) {
            CatnipServices.NETWORK.sendToServer(new ChainPackageInteractionPacket(ChainConveyorInteractionHandler.selectedLift, ChainConveyorInteractionHandler.selectedConnection, ChainConveyorInteractionHandler.selectedChainPosition, false));
        };
    };
};
