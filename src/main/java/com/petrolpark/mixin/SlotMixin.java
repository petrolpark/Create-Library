package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.decay.ageing.AgeingContainerWrapper;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * If an Item is removed from a Barrel, it should stop {@link AgeingRecipe ageing}. This mixin is to ensure that happens even when the Item is quick-swapped to a hotbar slot.
 */
@Mixin(Slot.class)
public abstract class SlotMixin {
    
    @Shadow
    public final Container container;

    public SlotMixin(Container container) {
        this.container = container;
        throw new AssertionError();
    };

    @WrapMethod(
        method = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"
    )
    public void wrapOnTake(Player player, ItemStack stack, Operation<Void> operation) {
        if (AgeingContainerWrapper.isAgeingContainer(container)) IApplyDecayRecipe.withAgeingDecayRemoved(player.level(), PetrolparkRecipeTypes.AGEING.get(), stack);
        operation.call(player, stack);
    };
};
