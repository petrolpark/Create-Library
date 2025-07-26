package com.petrolpark.util;

import java.util.Optional;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class AdvancementHelper {

    public static final <T> boolean test(Optional<T> predicate, T object) {
        if (predicate.isEmpty()) return true;
        return predicate.get().equals(object);
    };
    
    public static final boolean testBlock(Optional<Holder<Block>> blockPredicate, BlockState state) {
        if (blockPredicate.isEmpty()) return true;
        return state.is(blockPredicate.get());
    };

    public static final boolean testBlocks(Optional<HolderSet<Block>> blockPredicate, BlockState state) {
        if (blockPredicate.isEmpty()) return true;
        return state.is(blockPredicate.get());
    };

    public static final boolean testState(Optional<StatePropertiesPredicate> statePredicate, BlockState state) {
        if (statePredicate.isEmpty()) return true;
        return statePredicate.get().matches(state);
    };

    public static final boolean testFluid(Optional<FluidIngredient> fluidPredicate, FluidStack fluid) {
        if (fluidPredicate.isEmpty()) return true;
        return fluidPredicate.get().test(fluid);
    };
};
