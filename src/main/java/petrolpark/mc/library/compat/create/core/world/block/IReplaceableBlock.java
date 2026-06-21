package petrolpark.mc.library.compat.create.core.world.block;

import net.createmod.catnip.placement.IPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Used in {@link IPlacementHelper}s if this Block can replace or be replaced by others
 */
public interface IReplaceableBlock {

    public default boolean canBeReplaced(Level level, BlockPos pos, BlockState existingState, BlockState newState, Player player) {
        return getReplacedState(level, pos, existingState, newState, player) != null;
    };

    /**
     * Get the result of "replacing" {@code existingState} with {@code newState}. Essentially, what should happen be the result if both these BlockStates are placed in the same block space.
     * If both the Blocks of the existing BlockState and the one to be placed implement {@link IReplaceableBlock}, this should ideally be symmetrical but the already-placed BlockState will take priority.
     * @param level
     * @param pos
     * @param existingState
     * @param newState
     * @param player
     * @return A combined BlockState if the two states can go together, or {@code null} if they cannot be combined
     */
    public BlockState getReplacedState(Level level, BlockPos pos, BlockState existingState, BlockState newState, Player player);

};
