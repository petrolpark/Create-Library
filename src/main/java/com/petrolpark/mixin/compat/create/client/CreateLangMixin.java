package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.FluidContamination;
import com.petrolpark.util.Lang;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.lang.LangBuilder;
import net.neoforged.neoforge.fluids.FluidStack;

@Mixin(CreateLang.class)
public class CreateLangMixin {
    
    @ModifyReturnValue(
        method = "Lcom/simibubi/create/foundation/utility/CreateLang;fluidName(Lnet/neoforged/neoforge/fluids/FluidStack;)Lnet/createmod/catnip/lang/LangBuilder;",
        at = @At("TAIL")
    )
    private static LangBuilder petrolpark$addContaminants(LangBuilder original, FluidStack stack) {
        if (!PetrolparkConfigs.client().createShowContaminantsInFluidName.get()) return original;
        return Lang.appendContaminants(original, FluidContamination.get(stack));
    };
};
