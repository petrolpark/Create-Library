package com.petrolpark.util;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.petrolpark.core.world.fluid.FluidMixer;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidHelper {
    
    public static final boolean equalIgnoringComponents(FluidStack stack1, FluidStack stack2, DataComponentType<?> ...ignoredComponentTypes) {
        if (stack1.getFluid() != stack2.getFluid()) return false;
        return DataComponentHelper.equalIgnoring(stack1.getComponents(), stack2.getComponents(), type -> Stream.of(ignoredComponentTypes).anyMatch(type::equals));
    };

    public static final int fillTankWithMixer(final RegistryAccess registryAccess, FluidTank tank, FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !tank.isFluidValid(resource)) return 0;
        FluidStack toAdd = resource.copy();
        FluidStack result = FluidMixer.mixIn(registryAccess, tank.getFluid(), toAdd, tank.getCapacity(), action);
        if (action.execute()) tank.setFluid(result);
        return toAdd.getAmount();
    };

    public static final Hash.Strategy<? super FluidStack> TYPE_AND_COMPONENTS_HASH_STRATEGY = new Hash.Strategy<FluidStack>() {

        @Override
        public int hashCode(FluidStack o) {
            return FluidStack.hashFluidAndComponents(o);
        };

        @Override
        public boolean equals(FluidStack a, FluidStack b) {
            return Objects.equals(a, b) || (a != null && b != null && a.isEmpty() == b.isEmpty() && FluidStack.isSameFluidSameComponents(a, b));
        };

    };

    public static Set<FluidStack> createTypeAndComponentsSet() {
        return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_COMPONENTS_HASH_STRATEGY);
    };
};
