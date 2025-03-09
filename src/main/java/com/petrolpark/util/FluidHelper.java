package com.petrolpark.util;

import com.petrolpark.fluid.FluidMixer;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidHelper {
    
    public static boolean equalIgnoringTags(FluidStack stack1, FluidStack stack2, String ...ignoredTagKeys) {
        if (stack1.getFluid() != stack2.getFluid()) return false;
        return NBTHelper.equalIgnoring(stack1.getTag(), stack2.getTag(), ignoredTagKeys);
    };

    public static int fillTankWithMixer(FluidTank tank, FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !tank.isFluidValid(resource)) return 0;
        FluidStack toAdd = resource.copy();
        FluidStack result = FluidMixer.mixIn(tank.getFluid(), toAdd, tank.getCapacity(), action);
        if (action.execute()) tank.setFluid(result);
        return toAdd.getAmount();
    };
};
