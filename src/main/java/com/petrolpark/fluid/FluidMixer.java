package com.petrolpark.fluid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.petrolpark.contamination.FluidContamination;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidMixer {

    protected static final Set<IFluidMixer> MIXERS = new HashSet<>();

    public static void register(IFluidMixer mixer) {
        MIXERS.add(mixer);
    };

    static {
        register(FluidContamination.MIXER); // Default Mixer which replicates vanilla behaviour
    };

    /**
     * Mix several Fluids into another.
     * @param maxResultAmount
     * @param fluidStacks
     * @return An optional containing a new Fluid Stack if the Fluids can be mixed, or an empty optional if they cannot
     */
    public static Optional<FluidStack> mix(int maxResultAmount, FluidStack ...fluidStacks) {
        if (fluidStacks.length <= 1) return Optional.empty();
        IFluidMixer currentMixer = null;
        int currentPriority = 0;
        List<IFluidMixer> afterMixers = new ArrayList<>(MIXERS.size());
        for (IFluidMixer mixer : MIXERS) {
            int priority = mixer.getMixPriority(fluidStacks);
            if (priority >= 0) afterMixers.add(mixer);
            if (priority > currentPriority) {
                currentPriority = priority;
                currentMixer = mixer;
            };
        };
        if (currentMixer == null) return Optional.empty();
        FluidStack result = currentMixer.mix(fluidStacks);
        for (IFluidMixer mixer : afterMixers) mixer.afterMix(result, fluidStacks);
        return Optional.of(result);
    };

    /**
     * Mix one Fluid Stack into another.
     * @param maxResultAmount
     * @param baseFluidStack
     * @param addedFluidStack The amount of this will be set to the amount which was actually added, so pass a copy
     * @return A new Fluid Stack
     */
    public static FluidStack mixIn(FluidStack baseFluidStack, FluidStack addedFluidStack, int maxResultAmount, FluidAction action) {
        if (baseFluidStack.isEmpty()) {
            addedFluidStack.setAmount(Math.min(addedFluidStack.getAmount(), maxResultAmount));
            return addedFluidStack.copy();
        } else if (addedFluidStack.isEmpty()) {
            return baseFluidStack;
        };
        IFluidMixer currentMixer = null;
        int currentPriority = 0;
        List<IFluidMixer> afterMixers = new ArrayList<>(MIXERS.size());
        for (IFluidMixer mixer : MIXERS) {
            int priority = mixer.getMix2Priority(baseFluidStack, addedFluidStack);
            if (priority >= 0) afterMixers.add(mixer);
            if (priority > currentPriority) {
                currentPriority = priority;
                currentMixer = mixer;
            };
        };
        if (currentMixer != null) {
            addedFluidStack.setAmount(currentMixer.getAmountToMixIn(maxResultAmount, baseFluidStack, addedFluidStack));
            FluidStack result = currentMixer.mix2(baseFluidStack, addedFluidStack);
            if (afterMixers.isEmpty() || action.simulate()) return result;
            FluidStack[] fluidStacks = new FluidStack[]{baseFluidStack, addedFluidStack};
            for (IFluidMixer mixer : afterMixers) mixer.afterMix(result, fluidStacks);
            return result; 
        } else {
            addedFluidStack.setAmount(0);
            return baseFluidStack; // No Mixer exists that can mix these
        }
    };
    
    public interface IFluidMixer {

        /**
         * Get the maximum amount of {@code addedFluid} which can be mixed in such that the {@link IFluidMixer#mix2(FluidStack, FluidStack) mixing result} amount is no larger than {@code maxResultAmount}.
         * <b>Do not modify either Fluid Stack.</b>
         * @param baseFluidStack
         * @param addedFluidStack
         * @return Integer less than or equal to the amount of {@code addedFluid}
         */
        public default int getAmountToMixIn(int maxResultAmount, FluidStack baseFluidStack, FluidStack addedFluidStack) {
            return Math.min(addedFluidStack.getAmount(), Math.max(0, maxResultAmount - baseFluidStack.getAmount()));
        };

        /**
         * Shortcut version of {@link IFluidMixer#getMixPriority(FluidStack...)} for 2 Fluids only.
         */
        public default int getMix2Priority(FluidStack fluidStack1, FluidStack fluidStack2) {
            return getMixPriority(fluidStack1, fluidStack2);
        };
  
        /**
         * Decide whether this {@link IFluidMixer} is applicable to this combination of Fluid Stacks, and if so, what priority this mixer has for mixing them.
         * <b>Do not modify any Fluid Stack.</b>
         * @param fluidStacks Stacks to mix
         * @return Negative number to not use this mixer, {@code 0} to only call {@link IFluidMixer#afterMix(FluidStack, FluidStack...)}, or a positive priority to bid to call {@link IFluidMixer#mix(FluidStack...)}.
         */
        public int getMixPriority(FluidStack ...fluidStacks);

        /**
         * Shortcut version of {@link IFluidMixer#mix(FluidStack...)} for 2 Fluids only.
         * The amount does not necessarily have to be the sum of amounts of each input Fluid Stack, but if it is not make sure {@link IFluidMixer#getAmountToMixIn(int, FluidStack, FluidStack)} is properly defined.
         */
        public default FluidStack mix2(FluidStack fluidStack1, FluidStack fluidStack2) {
            return mix(fluidStack1, fluidStack2);
        };

        /**
         * Called on only one {@link IFluidMixer} whenever Fluids are mixed.
         * <b>Do not modify any Fluid Stack.</b>
         * @param fluidStacks
         * @return A new Fluid Stack instance
         */
        public FluidStack mix(FluidStack ...fluidStacks);

        /**
         * Called on <i>every</i> {@link IFluidMixer} after the highest-priority {@link IFluidMixer} has {@link IFluidMixer#mix(FluidStack...) mixed} them.
         * <b>Do not change the amount of Fluid.</b>
         * @param result
         * @param fluidStacks Input Fluid Stacks
         */
        public void afterMix(FluidStack result, FluidStack ...fluidStacks);

    };
};
