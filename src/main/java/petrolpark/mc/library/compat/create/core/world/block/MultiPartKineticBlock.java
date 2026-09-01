package petrolpark.mc.library.compat.create.core.world.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import petrolpark.mc.library.compat.create.core.world.block.multiPart.CreateMultiPartBlock.ICreatePart;

/**
 * {@link petrolpark.mc.library.compat.create.core.world.block.multiPart.MultiPartKineticBlock Moved} in 1.5.2 but copy here for backwards compatibility
 * TODO remove in 1.6.0
 */
@Deprecated(forRemoval = true, since = "1.5.2")
public abstract class MultiPartKineticBlock<PART extends ICreatePart> extends petrolpark.mc.library.compat.create.core.world.block.multiPart.MultiPartKineticBlock<PART> {

    protected MultiPartKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };
    
};
