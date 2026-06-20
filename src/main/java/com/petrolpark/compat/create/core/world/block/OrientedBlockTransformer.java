package com.petrolpark.compat.create.core.world.block;

import com.petrolpark.core.world.block.OrientedBlock;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries.BlockTransformer;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.world.level.block.state.BlockState;

public class OrientedBlockTransformer implements BlockTransformer {

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return state.setValue(OrientedBlock.ORIENTATION, state.getValue(OrientedBlock.ORIENTATION).mirror(transform.mirror).rotate(transform.rotationAxis, transform.rotation));
    };
    
};
