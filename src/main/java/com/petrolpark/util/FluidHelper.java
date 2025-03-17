package com.petrolpark.util;

import com.petrolpark.fluid.FluidMixer;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidHelper {
    
    public static boolean equalIgnoringComponents(FluidStack stack1, FluidStack stack2, DataComponentType<?> ...ignoredComponentTypes) {
        if (stack1.getFluid() != stack2.getFluid()) return false;
        return DataComponentHelper.equalIgnoring(stack1.getComponents(), stack2.getComponents(), ignoredComponentTypes);
    };

    public static int fillTankWithMixer(final RegistryAccess registryAccess, FluidTank tank, FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !tank.isFluidValid(resource)) return 0;
        FluidStack toAdd = resource.copy();
        FluidStack result = FluidMixer.mixIn(registryAccess, tank.getFluid(), toAdd, tank.getCapacity(), action);
        if (action.execute()) tank.setFluid(result);
        return toAdd.getAmount();
    };
};
