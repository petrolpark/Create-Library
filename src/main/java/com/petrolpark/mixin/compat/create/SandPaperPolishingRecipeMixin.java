package com.petrolpark.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(SandPaperPolishingRecipe.class)
public class SandPaperPolishingRecipeMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/content/equipment/sandPaper/SandPaperPolishingRecipe;applyPolish(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        remap = false,
        cancellable = true
    )
    private static void inApplyPolish(Level world, Vec3 position, ItemStack stack, ItemStack sandPaperStack, CallbackInfoReturnable<ItemStack> cir) {
        ItemDecay.startDecay(cir.getReturnValue());
        if (PetrolparkConfigs.server().createSandingRecipesPropagateContaminants.get()) ItemContamination.get(cir.getReturnValue()).contaminateAll(ItemContamination.get(stack).streamAllContaminants());
    };
};
