package petrolpark.mc.library.mixin.compat.jei.client;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.util.Lang;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.helpers.ModIdHelper;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ModIdHelper.class, remap = false)
public abstract class ModIdHelperMixin implements IModIdHelper {

    private static final String SHARED_FEATURE_ID_KEY = Petrolpark.MOD_ID + "shared";
    private static final String DELIMITER = ",";

    @WrapOperation(
        method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForTooltip(Lmezz/jei/api/ingredients/ITypedIngredient;)Ljava/util/Optional;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 0
        ),
        require = 1
    )
    private Object petrolpark$getSharedFeatureModIds(Function<?, ?> instance, Object argument, Operation<Object> original) {
        Object jeiResult = original.call(instance, argument);
        if (argument instanceof ITypedIngredient<?> typedIngredient
            && typedIngredient.getIngredient() instanceof ItemStack stack
            && stack.getItem() instanceof ISharedFeature sharedFeature
        ) {
            SharedFeatureFlag featureFlag = sharedFeature.getSharedFeatureFlag();
            if (featureFlag.enabled()) return SHARED_FEATURE_ID_KEY + DELIMITER + featureFlag.streamUsers().map(Mods::getId).collect(Collectors.joining(DELIMITER));
        };
        return jeiResult;
    };
    
    @WrapMethod(
        method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForModId(Ljava/lang/String;)Ljava/lang/String;"
    )
    public String petrolpark$formatSharedFeatureModIds(String modid, Operation<String> operation) {
        String[] split = modid.split(DELIMITER);
        if (split.length > 1 && split[0].equals(SHARED_FEATURE_ID_KEY)) {
            return Lang.shortList(Stream.of(Arrays.copyOfRange(split, 1, split.length))
                .map(operation::call)
                .toArray(String[]::new));
        };
        return operation.call(modid);
    };
};
