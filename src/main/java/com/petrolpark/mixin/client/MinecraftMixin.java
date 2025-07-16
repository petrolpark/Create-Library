package com.petrolpark.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.petrolpark.core.extendedinventory.ExtendedInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    private static final int VANILLA_INVENTORY_MENU_SLOTS = 46;

    @Shadow
    public LocalPlayer player;
    
    /**
     * Ensure picking a Block targets the correct Slot of the InventoryMenu (as the Slots of the Inventory Menu do not match up to the Slot indicies of the Inventory itself).
     * @param gameMode
     * @param stack
     * @param slotID
     */
    @Redirect(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private void redirectHandleCreativeModeItemAdd(MultiPlayerGameMode gameMode, ItemStack stack, int slotID) {
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isPresent()) {
            ExtendedInventory inv = invOp.get();
            if (!ExtendedInventory.isVanillaHotbarSlot(inv.getSelectedHotbarIndex())) {
                gameMode.handleCreativeModeItemAdd(stack, inv.selected - inv.getExtraInventoryStartSlotIndex() + VANILLA_INVENTORY_MENU_SLOTS);
            };
        };
        gameMode.handleCreativeModeItemAdd(stack, slotID); // Default behaviour
    };
};
