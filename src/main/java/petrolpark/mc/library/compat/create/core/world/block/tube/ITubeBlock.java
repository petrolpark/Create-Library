package petrolpark.mc.library.compat.create.core.world.block.tube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITubeBlock {
    
    public double getTubeSegmentRadius();

    public double getTubeSegmentLength();

    /**
     * In radians.
     */
    public double getTubeMaxAngle();

    public int getItemsForTubeLength(double length);

    /**
     * Must match {@link TubeBlockItem#getConnectingFace(net.minecraft.world.item.context.BlockPlaceContext)}.
     * @param level
     * @param pos
     * @param state
     */
    public Direction getTubeConnectingFace(Level level, BlockPos pos, BlockState state);

    /**
     * Called on the <em>controller</em> only, after all validations.
     * @param level
     * @param spline
     */
    public void connectTube(Level level, TubeSpline spline);

    public default boolean tryReconnect(UseOnContext context) {
        return TubeBehaviour.get(context.getLevel(), context.getClickedPos()).map(tube -> tube.reconnect(context.getPlayer(), true)).orElse(false);
    };
};
