package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.IDecayingItem;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Mixin(RecipeApplier.class)
public class RecipeApplierMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/foundation/recipe/RecipeApplier;applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;)Ljava/util/List;",
        at = @At("RETURN"),
        remap = false,
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void inApplyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe, CallbackInfoReturnable<List<ItemStack>> cir, List<ItemStack> stacks) {
        if (PetrolparkConfig.SERVER.createOtherRecipesPropagateContaminants.get()) {
            IContamination<?, ?> inputContamination = ItemContamination.get(stackIn);
            stacks.stream().map(ItemContamination::get).forEach(c -> c.contaminateAll(level.registryAccess(), inputContamination.streamAllContaminants()));
        };
        stacks.forEach(IDecayingItem::startDecay);
    };
};
