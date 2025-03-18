package com.petrolpark.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow
    private final Level level;
    
    public SmithingMenuMixin(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
        throw new AssertionError();
    };

    @Inject(
        method = "Lnet/minecraft/world/inventory/SmithingMenu;createResult()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
            ordinal = 1
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void inCreateResult(CallbackInfo ci, List<SmithingRecipe> list, RecipeHolder<SmithingRecipe> recipeHolder, ItemStack result) {
        if (PetrolparkConfig.SERVER.smithingPropagatesContaminants.get()) {
            ItemContamination.perpetuateSingle(level.registryAccess(), Stream.of(inputSlots.getItem(1), inputSlots.getItem(2)), result);
        };
        IDecayingItem.startDecay(result);
    };
};
