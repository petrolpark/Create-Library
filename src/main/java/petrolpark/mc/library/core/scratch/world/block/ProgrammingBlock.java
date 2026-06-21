package petrolpark.mc.library.core.scratch.world.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petrolpark.mc.library.core.world.block.OrientedBlock;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ProgrammingBlock extends OrientedBlock implements EntityBlock, ISharedFeature {

    public ProgrammingBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newBlockEntity'");
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.PROGRAMMING_BLOCK;
    };
    
};
