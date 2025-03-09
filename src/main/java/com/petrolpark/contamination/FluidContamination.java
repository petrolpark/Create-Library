package com.petrolpark.contamination;

import java.util.stream.Stream;

import com.petrolpark.fluid.FluidMixer.IFluidMixer;
import com.petrolpark.util.FluidHelper;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidContamination extends Contamination<Fluid, FluidStack> {

    public static final String TAG_KEY = "Contamination";

    public static IContamination<?, ?> get(FluidStack stack) {
        if (!Contaminables.FLUID.isContaminableStack(stack)) return new FluidContamination(stack);
        return IncontaminableContamination.INSTANCE;
    };

    public static void perpetuate(Stream<FluidStack> inputs, FluidStack output) {
        perpetuate(inputs, Stream.of(output));
    };

    public static void perpetuate(Stream<FluidStack> inputs, Stream<FluidStack> outputs) {
        IContamination.perpetuate(inputs, outputs, FluidContamination::get);
    };

    protected FluidContamination(FluidStack stack) {
        super(stack);
        if (stack.getTag() != null && stack.getTag().contains(TAG_KEY, Tag.TAG_LIST)) readNBT(stack.getTag().getList(TAG_KEY, Tag.TAG_STRING));
    };

    @Override
    public Contaminable<Fluid, FluidStack> getContaminable() {
        return Contaminables.FLUID;
    };

    @Override
    public Fluid getType() {
        return stack.getFluid();
    };

    @Override
    public double getAmount() {
        return stack.getAmount();
    };

    @Override
    public void save() {
        stack.removeChildTag(TAG_KEY);
        if (!orphanContaminants.isEmpty()) stack.getOrCreateTag().put(TAG_KEY, writeNBT());
    };

    public static final IFluidMixer MIXER = new IFluidMixer() {

        @Override
        public int getMix2Priority(FluidStack fluidStack1, FluidStack fluidStack2) {
            return FluidHelper.equalIgnoringTags(fluidStack1, fluidStack2, TAG_KEY) ? 1 : -1;
        };

        @Override
        public int getMixPriority(FluidStack... fluidStacks) {
            FluidStack fluidStack0 = fluidStacks[0];
            for (int i = 1; i < fluidStacks.length; i++) {
                if (!FluidHelper.equalIgnoringTags(fluidStack0, fluidStacks[i], TAG_KEY)) return -1;
            };
            return 1;
        };

        @Override
        public FluidStack mix2(FluidStack fluidStack1, FluidStack fluidStack2) {
            FluidStack result = fluidStack1.copy();
            result.grow(fluidStack2.getAmount());
            return result;
        };

        @Override
        public FluidStack mix(FluidStack... fluidStacks) {
            FluidStack result = fluidStacks[0];
            for (int i = 1; i < fluidStacks.length; i++) {
                result.grow(fluidStacks[i].getAmount());
            };
            return result;
        };

        @Override
        public void afterMix(FluidStack result, FluidStack... fluidStacks) {
            perpetuate(Stream.of(fluidStacks), result);
        };
        
    };
    
};
