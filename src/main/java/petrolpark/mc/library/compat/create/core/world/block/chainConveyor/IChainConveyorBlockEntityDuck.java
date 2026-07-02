package petrolpark.mc.library.compat.create.core.world.block.chainConveyor;

import java.util.List;
import java.util.Map;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;

import net.minecraft.core.BlockPos;

/**
 * {@link ChainConveyorBlockEntity} duck interface for Petrolpark Library's {@link ChainConveyorArmInteractionPoint}.
 */
public interface IChainConveyorBlockEntityDuck {
    
    public List<ChainConveyorPackage> getLoopingPackages();

    public Map<BlockPos, List<ChainConveyorPackage>> getTravellingPackages();
};
