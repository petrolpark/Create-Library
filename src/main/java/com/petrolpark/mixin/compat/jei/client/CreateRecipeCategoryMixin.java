package com.petrolpark.mixin.compat.jei.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.compat.jei.FlagsTooltipCallback;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeSlotBuilder;

@Mixin(CreateRecipeCategory.class)
public class CreateRecipeCategoryMixin {
    
    @ModifyReturnValue(
        method = "addFluidSlot",
        at = @At("TAIL")
    )
    private static IRecipeSlotBuilder petrolpark$addFlags(IRecipeSlotBuilder original) {
        return original.addRichTooltipCallback(FlagsTooltipCallback.INSTANCE);
    };
};
