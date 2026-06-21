package petrolpark.mc.library.compat.create.core.world.block.entity.basin;

import java.util.Optional;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A {@link BasinOperatingBlockEntity} which works directly above the Basin rather than two Blocks above it.
 */
public abstract class DirectlyAboveBasinOperatingBlockEntity extends AdvancedBasinOperatingBlockEntity {

    public DirectlyAboveBasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    @SuppressWarnings("null") // We already checked level is not null
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null) return Optional.empty();
        if (level.getBlockEntity(getBlockPos().below()) instanceof BasinBlockEntity basinBE) return Optional.of(basinBE);
        return Optional.empty();
    };
    
};
