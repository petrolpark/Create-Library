package petrolpark.mc.library.compat.create.core.world.block.multi;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.world.level.block.state.BlockState;

@ApiStatus.Experimental
public interface IWrappedMultiPartBehaviour<M extends IMulti<? super M>> {

    public void setWrappedBlockState(BlockState state);
    
    public BlockState getWrappedBlockState();

    public default void transformWrappedBlockState(StructureTransform transform) {
        setWrappedBlockState(transform.apply(getWrappedBlockState()));
    };
};
