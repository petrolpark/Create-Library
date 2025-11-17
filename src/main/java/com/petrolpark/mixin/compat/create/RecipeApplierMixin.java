package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Mixin(RecipeApplier.class)
public class RecipeApplierMixin {

    @ModifyReturnValue(
        method = "Lcom/simibubi/create/foundation/recipe/RecipeApplier;applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;",
        at = @At("RETURN"),
        remap = false
    )
    private static List<ItemStack> modifyApplyRecipeOn(List<ItemStack> original, Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder) {
        if (PetrolparkConfigs.server().createOtherRecipesPropagateContaminants.get()) {
            IContamination<?, ?> inputContamination = ItemContamination.get(stackIn);
            original.stream().map(ItemContamination::get).forEach(c -> c.contaminateAll(inputContamination.streamAllContaminants()));
        };
        original.forEach(ItemDecay::startDecay);
        return original;
    };
};
