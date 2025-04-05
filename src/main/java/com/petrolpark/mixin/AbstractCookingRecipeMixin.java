package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.core.contamination.ItemContamination;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

@Mixin(AbstractCookingRecipe.class)
public class AbstractCookingRecipeMixin {
  
    @Inject(
        method = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inAssemble(SingleRecipeInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        if (PetrolparkConfig.SERVER.cookingPropagatesContaminants.get()) ItemContamination.get(cir.getReturnValue()).contaminateAll(ItemContamination.get(input.item()).streamAllContaminants());
    };
};
