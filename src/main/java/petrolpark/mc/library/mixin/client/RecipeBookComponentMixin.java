package petrolpark.mc.library.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import petrolpark.mc.library.PetrolparkClient;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventory;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventoryClientHandler;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {

    @Shadow
    private int xOffset;

    @Shadow
    private boolean widthTooNarrow;
    
    /**
     * Shift the Recipe Book out of the way of the Extended Inventory.
     * @param ci
     */
    @Inject(
        method = "initVisuals",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;width:I"
        )
    )
    public void petrolpark$moveRecipeBookComponent(CallbackInfo ci) {
        if (!widthTooNarrow && ExtendedInventory.enabled()) xOffset += - PetrolparkClient.EXTENDED_INVENTORY_HANDLER.getLeftmostX() + ExtendedInventoryClientHandler.INVENTORY_SPACING;
    };
};
