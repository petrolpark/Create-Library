package com.petrolpark.mixin.compat.create.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.flags.FluidFlagPole;
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
    private static LangBuilder petrolpark$addFlags(LangBuilder original, FluidStack stack) {
        if (!PetrolparkConfigs.client().createShowFlagsInFluidName.get()) return original;
        return Lang.appendFlags(original, FluidFlagPole.get(stack));
    };
};
