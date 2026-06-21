package petrolpark.mc.library.mixin.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventory;

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
    @WrapOperation(
        method = "pickBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private void petrolpark$addItemsToExtendedInventorySlots(MultiPlayerGameMode gameMode, ItemStack stack, int slotID, Operation<Void> original) {
        Optional<ExtendedInventory> invOp = ExtendedInventory.get(player);
        if (invOp.isPresent()) {
            ExtendedInventory inv = invOp.get();
            if (!ExtendedInventory.isVanillaHotbarSlot(inv.getSelectedHotbarIndex())) {
                original.call(stack, inv.selected - inv.getExtraInventoryStartSlotIndex() + VANILLA_INVENTORY_MENU_SLOTS);
            };
        };
        original.call(gameMode, stack, slotID); // Default behaviour
    };
};
