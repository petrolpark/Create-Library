package com.petrolpark.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.petrolpark.core.extendedinventory.ExtendedInventory;
import com.petrolpark.core.extendedinventory.ExtendedInventoryClientHandler;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    abstract Player getCameraPlayer();

    @Shadow
    public abstract void renderSlot(GuiGraphics graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed);
  
    /**
     * Move the ItemStack in the Offhand out of the way of the extended Hotbar.
     * @param gui
     * @param graphics
     * @param x
     * @param y
     * @param deltaTracker
     * @param player
     * @param stack
     * @param seed
     */
    @Redirect(
        method = "renderItemHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private void renderOffhandItemOffset(Gui gui, GuiGraphics graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed) {
        if (stack == player.getOffhandItem()) { // If we're rendering the offhand Item
            Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
            if (invOp.isEmpty()) return;
            ExtendedInventory inv = invOp.get();
            if (player.getMainArm().getOpposite() == HumanoidArm.LEFT) {
                x -= 20 * ExtendedInventoryClientHandler.getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
            } else {
                x += 20 * ExtendedInventoryClientHandler.getRightExtraHotbarSlots(inv.getExtraHotbarSlots());
            };
        };
        renderSlot(graphics, x, y, deltaTracker, player, stack, seed);
    };

    /**
     * Move the background of the Offhand Slot out of the way of the extended Hotbar.
     * @param graphics
     * @param atlasLocation
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Redirect(
        method = "renderItemHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private void renderOffhandBackgroundOffset(GuiGraphics graphics, ResourceLocation atlasLocation, int x, int y, int width, int height) {
        if (atlasLocation == Gui.HOTBAR_OFFHAND_LEFT_SPRITE || atlasLocation == Gui.HOTBAR_OFFHAND_RIGHT_SPRITE) { // If we're rendering the offhand background
            Optional<ExtendedInventory> invOp = ExtendedInventory.get(getCameraPlayer());
            if (invOp.isEmpty()) return;
            ExtendedInventory inv = invOp.get();
            graphics.pose().pushPose();
            if (getCameraPlayer().getMainArm().getOpposite() == HumanoidArm.LEFT) {
                x -= 20 * ExtendedInventoryClientHandler.getLeftExtraHotbarSlots(inv.getExtraHotbarSlots());
            } else {
                x += 20 * ExtendedInventoryClientHandler.getRightExtraHotbarSlots(inv.getExtraHotbarSlots());
            };
            graphics.pose().popPose();
        };
        graphics.blitSprite(atlasLocation, x, y, width, height);
    };
};
