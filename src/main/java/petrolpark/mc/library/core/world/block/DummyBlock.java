package petrolpark.mc.library.core.world.block;

import petrolpark.mc.library.mixin.BlockMixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A Block never registered to {@link Registries#BLOCK}.
 * It will not have an {@link Block#builtInRegistryHolder intrusive holder} created.
 * This is obviously very unsafe, and this class should only be used if the way in which the Block object will be used is known precisely.
 * @see BlockMixin
 */
public abstract class DummyBlock extends Block {

    public DummyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };
    
};
