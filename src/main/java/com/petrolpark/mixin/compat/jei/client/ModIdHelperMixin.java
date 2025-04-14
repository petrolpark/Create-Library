package com.petrolpark.mixin.compat.jei.client;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.util.Lang;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.library.helpers.ModIdHelper;
import net.minecraft.world.item.ItemStack;

@Mixin(ModIdHelper.class)
public abstract class ModIdHelperMixin implements IModIdHelper {

    private static final String SHARED_FEATURE_ID_KEY = "petrolparkshared";
    private static final String DELIMITER = ",";

    @WrapOperation(
        method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForTooltip(Lmezz/jei/api/ingredients/ITypedIngredient;)Ljava/util/Optional;",
        at = @At(
            value = "INVOKE",
            target = "Lmezz/jei/api/ingredients/IIngredientHelper;getDisplayModId(Ljava/lang/Object;)Ljava/lang/String;"
        )
    )
    @SuppressWarnings("rawtypes")
    public String wrapGetDisplayModId(IIngredientHelper instance, Object ingredient, Operation<String> original) {
        if (ingredient instanceof ItemStack stack && stack.getItem() instanceof ISharedFeature sharedFeature) {
            SharedFeatureFlag featureFlag = sharedFeature.getSharedFeatureFlag();
            if (featureFlag.enabled()) return SHARED_FEATURE_ID_KEY + DELIMITER + featureFlag.streamUsers().map(Mods::getId).collect(Collectors.joining(DELIMITER));
        };
        return original.call(instance, ingredient);
    };
    
    @WrapMethod(
        method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForModId(Ljava/lang/String;)Ljava/lang/String;"
    )
    public String wrapGetModNameForModId(String modid, Operation<String> operation) {
        String[] split = modid.split(DELIMITER);
        if (split.length > 1 && split[0] == SHARED_FEATURE_ID_KEY) {
            return Lang.shortList(Stream.of(Arrays.copyOfRange(split, 1, split.length))
                .map(operation::call)
                .toArray(i -> new String[i]));
        };
        return operation.call(modid);
    };
};
