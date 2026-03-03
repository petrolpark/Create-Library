package com.petrolpark.mixin.compat.jei.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.util.Lang;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeCategoryTab;
import net.minecraft.network.chat.Component;

@Mixin(RecipeCategoryTab.class)
public abstract class RecipeCategoryTabMixin {
    
    @Shadow
    private IRecipeCategory<?> category;

    @WrapOperation(
        method = "Lmezz/jei/gui/recipes/RecipeCategoryTab;getTooltip()Lmezz/jei/common/gui/JeiTooltip;",
        at = @At(
            value = "INVOKE",
            target = "Lmezz/jei/api/helpers/IModIdHelper;getFormattedModNameForModId(Ljava/lang/String;)Ljava/lang/String;"
        )
    )
    public String petrolpark$getSharedFeatureModIds(IModIdHelper instance, String modid, Operation<String> original) {
        if (Petrolpark.MOD_ID.equals(modid) && category instanceof ISharedFeature sharedCategory) {
            return Lang.shortList(sharedCategory.getSharedFeatureFlag().streamUsers().map(original::call).map(Component::literal).toList(), 100).getString();
        } else return original.call(modid);
    };
};
