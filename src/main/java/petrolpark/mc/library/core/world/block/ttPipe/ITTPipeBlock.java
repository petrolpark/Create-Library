package petrolpark.mc.library.core.world.block.ttPipe;

import java.util.function.Consumer;

import petrolpark.mc.library.core.world.block.ttPipe.valve.IValve;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * "Transport Tycoon" Pipe, so named because I was inspired by the way train tracks can connect in Transport Tycoon Deluxe by Sid Myers
 */
public interface ITTPipeBlock<VALVE extends IValve<VALVE>> {
    
    public ITTPipeType<VALVE, ? extends ITTPipeConnectionHandler<VALVE, ?>> getPipeType();

    public void getPipeConnections(Level level, BlockPos pos, BlockState state, Direction inputFace, Consumer<TTPipeConnection<VALVE>> connections) throws TTPipeConnectionException;

    //TODO actually use this method
    public void updatePossibleConnections(Level level, BlockPos pos, BlockState state, Consumer<Vec3i> connectionLocations);
};
