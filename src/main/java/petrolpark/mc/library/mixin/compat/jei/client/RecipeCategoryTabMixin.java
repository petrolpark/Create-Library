package petrolpark.mc.library.mixin.compat.jei.client;

import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.ISharedFeature;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeCategoryTab;
import net.minecraft.network.chat.Component;

@Mixin(value = RecipeCategoryTab.class, remap = false)
public abstract class RecipeCategoryTabMixin {

    private static final String SHARED_FEATURE_ID_KEY = Petrolpark.MOD_ID + "shared";
    private static final String DELIMITER = ",";
    
    @Shadow
    @Final
    private IRecipeCategory<?> category;

    @WrapOperation(
        method = "Lmezz/jei/gui/recipes/RecipeCategoryTab;getTooltip()Lmezz/jei/common/gui/JeiTooltip;",
        at = @At(
            value = "INVOKE",
            target = "Lmezz/jei/api/helpers/IModIdHelper;getFormattedModNameComponentForModId(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;",
            ordinal = 0
        ),
        require = 1
    )
    private Component petrolpark$getSharedFeatureModIds(IModIdHelper instance, String modid, Operation<Component> original) {
        if (Petrolpark.MOD_ID.equals(modid) && category instanceof ISharedFeature sharedCategory) {
            String sharedModIds = sharedCategory.getSharedFeatureFlag().streamUsers().map(Mods::getId).collect(Collectors.joining(DELIMITER));
            if (!sharedModIds.isEmpty()) return original.call(instance, SHARED_FEATURE_ID_KEY + DELIMITER + sharedModIds);
        };
        return original.call(instance, modid);
    };
};
