package petrolpark.mc.library.mixin.compat.jei.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeCategoryTab;
import net.minecraft.network.chat.Component;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.util.Lang;

@Mixin(RecipeCategoryTab.class)
public abstract class RecipeCategoryTabMixin {
    
    @Shadow
    @Final
    private IRecipeCategory<?> category;

    @WrapOperation(
        method = "Lmezz/jei/gui/recipes/RecipeCategoryTab;getTooltip()Lmezz/jei/common/gui/JeiTooltip;",
        at = @At(
            value = "INVOKE",
            target = "Lmezz/jei/api/helpers/IModIdHelper;getFormattedModNameComponentForModId(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;"
        )
    )
    public Component petrolpark$getSharedFeatureModIds(IModIdHelper instance, String modid, Operation<Component> original) {
        if (category instanceof ISharedFeature sharedCategory) {
            return Lang.shortList(sharedCategory.getSharedFeatureFlag().streamUsers().map(Mods::getId).map(id -> original.call(instance, id)).toList(), 320);
        } else return original.call(instance, modid);
    };
};
