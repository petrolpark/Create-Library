package petrolpark.mc.library.compat.create.core.world.block.chainConveyor;

import java.util.List;
import java.util.Map;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;

import net.minecraft.core.BlockPos;

public interface IChainConveyorBlockEntityDuck {
    
    public List<ChainConveyorPackage> getLoopingPackages();

    public Map<BlockPos, List<ChainConveyorPackage>> getTravellingPackages();
};
