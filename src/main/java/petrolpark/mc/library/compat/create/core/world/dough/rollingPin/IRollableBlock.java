package petrolpark.mc.library.compat.create.core.world.dough.rollingPin;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public interface IRollableBlock {
    
    public boolean canBeRollingPinRolled(Level level, BlockPos pos, Direction horizontalLookingDirection);

    public void rollingPinRoll(Level level, BlockPos pos, Direction horizontalLookingDirection, boolean byPlayer);
};
