package com.petrolpark.core.flags;

import java.util.stream.Stream;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidFlagPole extends ComponentHolderFlagPole<Fluid, FluidStack> {

    public static final IFlagPole<?, ?> get(FluidStack stack) {
        if (!Flaggables.FLUID.isFlaggableStack(stack)) return new FluidFlagPole(stack);
        return UnflaggableFlagPole.INSTANCE;
    };

    public static void perpetuate(Stream<FluidStack> inputs, FluidStack output) {
        perpetuate(inputs, Stream.of(output));
    };

    public static void perpetuate(Stream<FluidStack> inputs, Stream<FluidStack> outputs) {
        IFlagPole.perpetuate(inputs, outputs, FluidFlagPole::get);
    };

    protected FluidFlagPole(FluidStack stack) {
        super(stack);
    };

    @Override
    public Flaggable<Fluid, FluidStack> getFlaggable() {
        return Flaggables.FLUID;
    };

    @Override
    public Fluid getType() {
        return stack.getFluid();
    };

    @Override
    public double getAmount() {
        return stack.getAmount();
    };

    // public static final IFluidMixer MIXER = new IFluidMixer() {

    //     @Override
    //     public int getMix2Priority(final HolderLookup.Provider registryAccess, FluidStack fluidStack1, FluidStack fluidStack2) {
    //         return FluidHelper.equalIgnoringComponents(fluidStack1, fluidStack2, PetrolparkDataComponents.ORPHAN_FLAGS) ? 1 : -1;
    //     };

    //     @Override
    //     public int getMixPriority(final HolderLookup.Provider registryAccess, FluidStack... fluidStacks) {
    //         FluidStack fluidStack0 = fluidStacks[0];
    //         for (int i = 1; i < fluidStacks.length; i++) {
    //             if (!FluidHelper.equalIgnoringComponents(fluidStack0, fluidStacks[i], PetrolparkDataComponents.ORPHAN_FLAGS)) return -1;
    //         };
    //         return 1;
    //     };

    //     @Override
    //     public FluidStack mix2(final HolderLookup.Provider registryAccess, FluidStack fluidStack1, FluidStack fluidStack2) {
    //         FluidStack result = fluidStack1.copy();
    //         result.grow(fluidStack2.getAmount());
    //         return result;
    //     };

    //     @Override
    //     public FluidStack mix(final HolderLookup.Provider registryAccess, FluidStack... fluidStacks) {
    //         FluidStack result = fluidStacks[0];
    //         for (int i = 1; i < fluidStacks.length; i++) {
    //             result.grow(fluidStacks[i].getAmount());
    //         };
    //         return result;
    //     };

    //     @Override
    //     public void afterMix(final HolderLookup.Provider registryAccess, FluidStack result, FluidStack... fluidStacks) {
    //         perpetuate(registryAccess, Stream.of(fluidStacks), result);
    //     };
        
    // };
    
};
