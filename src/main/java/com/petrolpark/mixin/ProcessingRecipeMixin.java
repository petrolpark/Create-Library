package com.petrolpark.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.itemdecay.IDecayingItem;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.world.item.ItemStack;

@Mixin(ProcessingRecipe.class)
public class ProcessingRecipeMixin {
    
    @Inject(
        method = "rollResults(Ljava/util/List;)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inRollResults(List<ProcessingOutput> rollableResults, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> results = cir.getReturnValue();
        results.forEach(s -> IDecayingItem.startDecay(s, 0));
        cir.setReturnValue(results);
    };
};
