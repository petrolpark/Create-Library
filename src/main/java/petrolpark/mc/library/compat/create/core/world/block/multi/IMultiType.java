package petrolpark.mc.library.compat.create.core.world.block.multi;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

/**
 * An enclosed cuboid Multi-Block.
 */
@ApiStatus.Experimental
public interface IMultiType<M extends IMulti<? super M>> {
    
    public BehaviourType<? extends MultiBehaviour<? extends M>> getBehaviourType();

    /**
     * Whether the {@link IMulti} can be moved as a whole by Create machinery.
     * If {@code false}, interior Blocks must be connected to the walls in order to form a valid {@link IMulti}.
     */
    public boolean isMoveable();
};
