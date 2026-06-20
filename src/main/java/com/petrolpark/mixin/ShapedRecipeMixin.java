package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.core.flags.recipe.IHandleFlagsMyselfRecipe;
import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Allow Shaped Recipes to propagate the Flags of the Ingredients to the result.
 */
@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin implements IHandleFlagsMyselfRecipe<CraftingInput> {

    @ModifyReturnValue(
        method = "Lnet/minecraft/world/item/crafting/ShapedRecipe;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN")
    )
    public ItemStack petrolpark$propagateFlagsAndStartDecay(ItemStack output, CraftingInput input, HolderLookup.Provider registries) {
        ItemDecay.startDecay(output);
        if (PetrolparkConfigs.server().shapedCraftingPropagatesFlags.get()) ItemFlagPole.perpetuateSingle(input.items().stream(), output);
        return output;
    };

    @Override
    public boolean isFlagsHandled(CraftingInput container, HolderLookup.Provider registries) {
        return PetrolparkConfigs.server().shapedCraftingPropagatesFlags.get();
    };
    

};
