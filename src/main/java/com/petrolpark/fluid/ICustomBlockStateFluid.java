package com.petrolpark.fluid;

import net.minecraft.world.level.block.state.BlockState;

public interface ICustomBlockStateFluid {
    
    /**
     * @return The BlockState corresponding to 1000mB of this Fluid
     */
    public BlockState getBlockState();
};
