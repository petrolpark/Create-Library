package com.petrolpark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.contamination.recipe.IHandleContaminationMyselfRecipe;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {
    
    @Inject(
        method = "Lnet/minecraft/world/inventory/CraftingMenu;slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;Lnet/minecraft/world/item/crafting/RecipeHolder;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void inSlotChangedCraftingGrid(
        AbstractContainerMenu menu,
        Level level,
        Player player,
        CraftingContainer craftSlots,
        ResultContainer resultSlots,
        RecipeHolder recipe,
        CallbackInfo ci, CraftingInput craftinginput, ServerPlayer serverplayer, ItemStack itemstack
    ) {
        if (!itemstack.isEmpty()) {
            ItemDecay.startDecay(itemstack);
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe); // For mystery reasons this cannot be localcaptured
            if (PetrolparkConfigs.server().craftingTablePropagatesContaminants.get() && optional.map(rh -> {
                if (rh.value() instanceof IHandleContaminationMyselfRecipe contamHandled) {
                    return !contamHandled.isContaminationHandled(craftinginput, level.registryAccess());
                } else return true;
            }).orElse(true)) {
                ItemContamination.perpetuateSingle(craftSlots.getItems().stream(), itemstack);
            };
        };
    };
};
