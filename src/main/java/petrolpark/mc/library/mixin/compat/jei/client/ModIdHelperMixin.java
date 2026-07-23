package petrolpark.mc.library.mixin.compat.jei.client;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.helpers.ModIdHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.util.Lang;

@Mixin(ModIdHelper.class)
public abstract class ModIdHelperMixin implements IModIdHelper {

    // private static final String SHARED_FEATURE_ID_KEY = Petrolpark.MOD_ID + "shared";
    // private static final String DELIMITER = ",";

    @WrapMethod(
        method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForTooltip(Lmezz/jei/api/ingredients/ITypedIngredient;)Ljava/util/Optional;"
    )
    public Optional<Component> petrolpark$getSharedFeatureModNames(ITypedIngredient<?> ingredient, Operation<Optional<Component>> original) {
        final ISharedFeature sharedFeature;
        switch (ingredient.getIngredient()) {
            case ISharedFeature ingredientSharedFeature -> {
                sharedFeature = ingredientSharedFeature;
            } case ItemStack itemStack -> {
                if (itemStack.getItem() instanceof ISharedFeature itemSharedFeature) sharedFeature = itemSharedFeature;
                else return original.call(ingredient);
            } default -> {
                return original.call(ingredient);
            }
        };

        if (sharedFeature.getSharedFeatureFlag().enabled()) return Optional.of(Lang.shortList(sharedFeature.getSharedFeatureFlag().streamUsers()
            .map(Mods::getId)
            .map(this::getFormattedModNameComponentForModId)
            .toList(), 320)
        );

        return original.call(ingredient);
    };

    // @WrapOperation(
    //     method = "Lmezz/jei/library/helpers/ModIdHelper;getModNameForTooltip(Lmezz/jei/api/ingredients/ITypedIngredient;)Ljava/util/Optional;",
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"
    //     )
    // )
    // public Object petrolpark$getSharedFeatureModIds(Function<ITypedIngredient<?>, String> instance, Object ingredient, Operation<Object> original) {
    //     if (ingredient instanceof ITypedIngredient<?> typedIngredient && typedIngredient.getIngredient() instanceof ItemStack stack && stack.getItem() instanceof ISharedFeature sharedFeature) {
    //         SharedFeatureFlag featureFlag = sharedFeature.getSharedFeatureFlag();
    //         if (featureFlag.enabled()) return SHARED_FEATURE_ID_KEY + DELIMITER + featureFlag.streamUsers().map(Mods::getId).collect(Collectors.joining(DELIMITER));
    //     };
    //     return original.call(instance, ingredient);
    // };
    
    // @WrapMethod(
    //     method = "Lmezz/jei/library/helpers/ModIdHelper;getFormattedModNameComponentForModId(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;"
    // )
    // public Component petrolpark$formatSharedFeatureModIds(String modid, Operation<Component> operation) {
    //     List<String> split = new ArrayList<>(Arrays.asList(modid.split(DELIMITER)));
    //     if (split.size() > 1 && split.remove(0).equals(SHARED_FEATURE_ID_KEY)) {
    //         return Lang.shortList(split.stream().map(operation::call).toList(), 320);
    //     };
    //     return operation.call(modid);
    // };
};
