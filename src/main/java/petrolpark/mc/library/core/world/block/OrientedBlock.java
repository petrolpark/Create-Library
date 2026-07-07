package petrolpark.mc.library.core.world.block;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import petrolpark.mc.library.util.Orientation;

public abstract class OrientedBlock extends Block {

    public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);

    public OrientedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    };

    @Override
    protected BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) {
        return state.setValue(ORIENTATION, state.getValue(ORIENTATION).rotate(Axis.Y, rotation));
    };

    @Override
    protected BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirror) {
        return state.setValue(ORIENTATION, state.getValue(ORIENTATION).mirror(mirror));
    };
    
};
