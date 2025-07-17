package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.contamination.recipe.IHandleContaminationMyselfRecipe;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Allow Shaped Recipes to propagate the Contaminants of the Ingredients to the result.
 */
@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin implements IHandleContaminationMyselfRecipe<CraftingInput> {

    @ModifyReturnValue(
        method = "Lnet/minecraft/world/item/crafting/ShapedRecipe;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN")
    )
    public ItemStack modifyAssemble(ItemStack output, CraftingInput input, HolderLookup.Provider registries) {
        ItemDecay.startDecay(output);
        if (PetrolparkConfigs.server().shapedCraftingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(input.items().stream(), output);
        return output;
    };

    @Override
    public boolean isContaminationHandled(CraftingInput container, HolderLookup.Provider registries) {
        return PetrolparkConfigs.server().shapedCraftingPropagatesContaminants.get();
    };
    

};
