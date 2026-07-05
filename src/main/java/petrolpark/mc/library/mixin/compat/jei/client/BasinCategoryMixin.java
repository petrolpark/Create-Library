package petrolpark.mc.library.mixin.compat.jei.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.simibubi.create.compat.jei.category.BasinCategory;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

@Mixin(BasinCategory.class)
public class BasinCategoryMixin {

    @Unique
    private List<ItemStack> petrolpark$superheatingFuels = null;

    @Unique
    private List<ItemStack> petrolpark$getSuperheatingFuels() {
        if (petrolpark$superheatingFuels == null) {
            petrolpark$superheatingFuels = BuiltInRegistries.ITEM.getDataMap(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS).entrySet().stream()
                .filter(entry -> entry.getValue().burnTime() > 0)
                .map(Map.Entry::getKey)
                .map(BuiltInRegistries.ITEM::get)
                .map(ItemStack::new)
                .toList();
        };
        return petrolpark$superheatingFuels;
    };
    
    @WrapOperation(
        method = "setRecipe",
        at = @At(
            value = "INVOKE",
            target = "addItemStack",
            ordinal = 2
        )
    )
    public IIngredientAcceptor<?> petrolpark$showAllSuperheatingFuels(IRecipeSlotBuilder recipeSlotBuilder, ItemStack stack, Operation<IIngredientAcceptor<?>> original) {
        return recipeSlotBuilder.addItemStacks(petrolpark$getSuperheatingFuels());
    };
};
