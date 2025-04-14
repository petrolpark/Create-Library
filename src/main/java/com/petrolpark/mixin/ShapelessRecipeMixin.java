package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.contamination.recipe.IHandleContaminationMyselfRecipe;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Allow Shapeless Recipes to propagate the Contaminants of the Ingredients to the result.
 */
@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements IHandleContaminationMyselfRecipe<CraftingInput> {

    @Inject(
        method = "Lnet/minecraft/world/item/crafting/ShapelessRecipe;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inAssemble(CraftingInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        ItemDecay.startDecay(cir.getReturnValue());
        if (PetrolparkConfigs.server().shapelessCraftingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(input.items().stream(), cir.getReturnValue());
    };

    @Override
    public boolean isContaminationHandled(CraftingInput input, HolderLookup.Provider registrie) {
        return PetrolparkConfigs.server().shapelessCraftingPropagatesContaminants.get();
    };
    

};
