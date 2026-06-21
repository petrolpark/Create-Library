package petrolpark.mc.library.core.world.block.ttPipe;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import petrolpark.mc.library.core.world.block.ttPipe.valve.IValve;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractStraightTTPipeBlock<VALVE extends IValve<VALVE>> extends RotatedPillarBlock implements ITTPipeBlock<VALVE> {

    protected final Map<Direction, TTPipeConnection<VALVE>> connections = Stream.of(Direction.values()).collect(Collectors.toMap(Function.identity(), dir -> new TTPipeConnection<>(dir.getNormal(), dir, null)));

    public AbstractStraightTTPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public void getPipeConnections(Level level, BlockPos pos, BlockState state, Direction inputFace, Consumer<TTPipeConnection<VALVE>> connections) {
        if (inputFace.getAxis() == state.getValue(AXIS)) connections.accept(this.connections.get(inputFace));
    };

    @Override
    public void updatePossibleConnections(Level level, BlockPos pos, BlockState state, Consumer<Vec3i> connectionLocations) {
        final Axis axis = state.getValue(AXIS);
        connectionLocations.accept(Direction.get(AxisDirection.POSITIVE, axis).getNormal());
        connectionLocations.accept(Direction.get(AxisDirection.NEGATIVE, axis).getNormal());
    };
    
};
