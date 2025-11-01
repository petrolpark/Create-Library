package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.core.item.decay.ItemDecay;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.world.item.ItemStack;

@Mixin(ProcessingRecipe.class)
public class ProcessingRecipeMixin {
    
    @ModifyReturnValue(
        method = "rollResults(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/List;",
        at = @At("RETURN"),
        remap = false
    )
    public List<ItemStack> modifyRollResults(List<ItemStack> original, List<ProcessingOutput> rollableResults) {
        original.forEach(s -> ItemDecay.startDecay(s, 0));
        return original;
    };
};
