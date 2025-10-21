package com.petrolpark.compat.create.core.fluid;

import java.util.Optional;

import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.neoforged.neoforge.fluids.crafting.SingleFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

public class CreateFluidHelper {
  
    public static Optional<FluidIngredient> toCreateIngredient(int amount, net.neoforged.neoforge.fluids.crafting.FluidIngredient ingredient) {
        if (ingredient instanceof TagFluidIngredient tag) return Optional.of(FluidIngredient.fromTag(tag.tag(), amount));
        if (ingredient instanceof SingleFluidIngredient single) return Optional.of(FluidIngredient.fromFluid(single.fluid().value(), amount));
        return Optional.empty();
    };
};
