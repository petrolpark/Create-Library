package petrolpark.mc.library.core.world.block;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class LogBlock extends RotatedPillarBlock {

    protected final Supplier<BlockState> strippedBlockState;

    public LogBlock(Properties properties, Supplier<BlockState> strippedBlock) {
        super(properties);
        this.strippedBlockState  = strippedBlock;
    };

    @Override
    public @Nullable BlockState getToolModifiedState(@Nonnull BlockState state, @Nonnull UseOnContext context, @Nonnull ItemAbility itemAbility, boolean simulate) {
        return itemAbility == ItemAbilities.AXE_STRIP ? strippedBlockState.get() : super.getToolModifiedState(state, context, itemAbility, simulate);
    };
    
};
