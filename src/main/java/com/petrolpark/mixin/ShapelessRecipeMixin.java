package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.recipe.contamination.IHandleContaminationMyselfRecipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements IHandleContaminationMyselfRecipe<CraftingInput> {

    @Inject(
        method = "Lnet/minecraft/world/item/crafting/ShapelessRecipe;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inAssemble(CraftingInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        IDecayingItem.startDecay(cir.getReturnValue());
        if (PetrolparkConfig.SERVER.shapelessCraftingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(registries, input.items().stream(), cir.getReturnValue());
    };

    @Override
    public boolean isContaminationHandled(CraftingInput input, HolderLookup.Provider registrie) {
        return PetrolparkConfig.SERVER.shapelessCraftingPropagatesContaminants.get();
    };
    

};
