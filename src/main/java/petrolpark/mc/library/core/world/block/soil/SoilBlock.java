package petrolpark.mc.library.core.world.block.soil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.shared.registry.SharedBlockEntityTypes;

@ApiStatus.Experimental
public class SoilBlock extends Block implements EntityBlock {

    public SoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    public void handlePrecipitation(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) level.getBlockEntity(pos, SharedBlockEntityTypes.SOIL.get()).ifPresent(be -> be.hydration.changeFluidAmount(10)); //TODO config
    };

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> blockEntityType) {
        return null;
    };

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new SoilBlockEntity(SharedBlockEntityTypes.SOIL.get(), pos, state);
    };
    
};
