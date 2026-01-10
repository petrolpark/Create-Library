package com.petrolpark.core.world.fluid;

import javax.annotation.Nonnull;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class InputOnlyFluidTank implements IFluidHandler, IFluidTank {

    protected int amount = 0;
    protected final int max;

    public InputOnlyFluidTank(int max) {
        this.max = max;
    };

    public abstract void onFluidChanged();

    public int setFluidAmount(int amount) {
        amount = Mth.clamp(amount, 0, max);
        if (this.amount == amount) return amount;
        this.amount = amount;
        onFluidChanged();
        return amount;
    };

    public int changeFluidAmount(int change) {
        return setFluidAmount(this.amount + change);
    };

    @Override
    public int getTanks() {
        return 1;
    };

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    };

    @Override
    public int getTankCapacity(int tank) {
        if (tank != 0) throw new IndexOutOfBoundsException(tank);
        return max;
    };

    @Override
    public int fill(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
        if (!isFluidValid(0, resource)) return 0;
        final int filled = Math.min(resource.getAmount(), max - amount);
        if (action.execute()) {
            amount += filled;
            onFluidChanged();
        };
        return filled;
    };

    @Override
    public FluidStack drain(@Nonnull FluidStack resource, @Nonnull FluidAction action) {
        return FluidStack.EMPTY;
    };

    @Override
    public FluidStack drain(int maxDrain, @Nonnull FluidAction action) {
        return FluidStack.EMPTY;
    };

    @Override
    public FluidStack getFluid() {
        return FluidStack.EMPTY;
    };

    @Override
    public int getFluidAmount() {
        return amount;
    };

    @Override
    public int getCapacity() {
        return max;
    };

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid(stack);
    };
    
};
