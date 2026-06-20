package com.petrolpark.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.core.world.item.decay.ItemDecay;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
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
    public void petrolpark$propagateFlagsAndStartDecay(CallbackInfo ci, SmithingRecipeInput smithingrecipeinput, List<SmithingRecipe> list, RecipeHolder<SmithingRecipe> recipeHolder, ItemStack result) {
        if (PetrolparkConfigs.server().smithingPropagatesFlags.get()) {
            ItemFlagPole.perpetuateSingle(Stream.of(inputSlots.getItem(1), inputSlots.getItem(2)), result);
        };
        ItemDecay.startDecay(result);
    };
};
