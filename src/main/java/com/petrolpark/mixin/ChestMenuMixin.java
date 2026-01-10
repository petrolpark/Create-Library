package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.decay.ageing.AgeingContainerWrapper;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

/**
 * If an Item is removed from a Barrel, it should stop {@link AgeingRecipe ageing}. This mixin is to ensure that happens even when the Item is shift-clicked out of the Barrel.
 */
@Mixin(ChestMenu.class)
public abstract class ChestMenuMixin extends AbstractContainerMenu {

    protected ChestMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        throw new AssertionError();
    };

    @WrapOperation(
        method = "Lnet/minecraft/world/inventory/ChestMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"
        )
    )
    public ItemStack wrapGetItem(Slot instance, Operation<ItemStack> original) {
        Level level;
        if (instance.container instanceof BarrelBlockEntity barrel) level = barrel.getLevel();
        else if (instance.container instanceof AgeingContainerWrapper ageingContainer && AgeingContainerWrapper.ageingInVanillaBarrelsEnabled()) level = ageingContainer.getLevel();
        else return original.call(instance);
        return IApplyDecayRecipe.withAgeingDecayRemoved(level, PetrolparkRecipeTypes.AGEING.get(), original.call(instance));
    };
    
};
