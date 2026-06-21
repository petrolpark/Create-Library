package petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft
.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class HorseMillBearingBlock extends KineticBlock implements IBE<HorseMillBearingBlockEntity>, ISharedFeature {

    public static final DirectionProperty FACING = BlockStateProperties.VERTICAL_DIRECTION;

    public HorseMillBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
    };

    @Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING).getOpposite();
	};

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
	public boolean showCapacityWithAnnotation() {
		return true;
	};

    @Override
	protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
		if (!player.mayBuild()) return ItemInteractionResult.FAIL;
		if (player.isShiftKeyDown()) return ItemInteractionResult.FAIL;
		if (stack.isEmpty()) {
			if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
			withBlockEntityDo(level, pos, be -> {
				if (be.isRunning()) be.disassemble();
				else be.assembleNextTick();
			});
			return ItemInteractionResult.SUCCESS;
		};
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	};

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		InteractionResult resultType = super.onWrenched(state, context);
		if (!context.getLevel().isClientSide() && resultType.consumesAction()) context.getLevel().getBlockEntity(context.getClickedPos(), SharedCreateBlockEntityTypes.HORSE_MILL_BEARING.get()).ifPresent(HorseMillBearingBlockEntity::disassemble);
		return resultType;
	};

    @Override
    public Class<HorseMillBearingBlockEntity> getBlockEntityClass() {
        return HorseMillBearingBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends HorseMillBearingBlockEntity> getBlockEntityType() {
        return SharedCreateBlockEntityTypes.HORSE_MILL_BEARING.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.HORSE_MILL;
    };
    
};
