package petrolpark.mc.library.compat.create.core.world.dough;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.FlagPoleBehaviour;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.IRollableBlock;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateBlockEntityTypes;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.core.world.block.IPickUpPutDownBlock;

@ParametersAreNonnullByDefault
public class DoughBlock extends Block implements IBE<DoughBlockEntity>, IRollableBlock, IPickUpPutDownBlock {

    public DoughBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return getBlockEntityOptional(level, pos).flatMap(DoughBlockEntity::getVoxelShape).orElse(Shapes.empty());
    };

    @Override
    public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        withBlockEntityDo(level, pos, be -> stack.applyComponents(be.collectComponents()));
        return stack;
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntityBehaviour.get(level, pos, FlagPoleBehaviour.TYPE).getFlagPole().flagAll(ItemFlagPole.get(stack).streamOrphanExtrinsicFlags());
    };

    @SuppressWarnings("null")
    public static final int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        return level == null && pos == null ? -1 : level.getBlockEntity(pos, PetrolparkCreateBlockEntityTypes.DOUGH.get()).map(be -> be.doughData.dough().tint()).orElse(-1);
    };

    @Override
    public boolean canBeRollingPinRolled(UseOnContext context) {
        return getBlockEntityOptional(context.getLevel(), context.getClickedPos()).map(be -> be.doughData.isRollable(context.getHorizontalDirection().getAxis() == Axis.Z)).orElse(false);
    };

    @Override
    public void rollingPinRoll(UseOnContext context) {
        final boolean lengthwise = context.getHorizontalDirection().getAxis() == Axis.Z;
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> be.modifyDough(dough -> dough.isRollable(lengthwise) ? dough.rolled(lengthwise, context.getPlayer() != null && !(context.getPlayer() instanceof FakePlayer)) : dough));
    };

    @Override
    public Class<DoughBlockEntity> getBlockEntityClass() {
        return DoughBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DoughBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.DOUGH.get();
    };
    
};
