package com.petrolpark.compat.create.core.world.block.crushingWheel;

import com.petrolpark.compat.create.registry.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;
import com.petrolpark.config.PetrolparkConfigs;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class EncasedCrushingWheelControllerBlock extends CrushingWheelControllerBlock implements EncasedBlock, IWrenchable {

    public static final void onItemUsed(UseItemOnBlockEvent event) {
        if (!PetrolparkConfigs.server().createEncasedCrushingWheels.get()) return;
        final BlockState state = event.getLevel().getBlockState(event.getPos());
        if (!AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(state) || !state.getValue(VALID)
            || !AllBlocks.BRASS_CASING.isIn(event.getItemStack())
        ) return;
        if (!event.getLevel().isClientSide()) {
            SharedCreateBlocks.ENCASED_CRUSHING_WHEEL_CONTROLLER.get().handleEncasing(state, event.getLevel(), event.getPos(), event.getItemStack(), event.getPlayer(), event.getHand(), event.getUseOnContext().getHitResult());
            AllBlocks.SHAFT.get().playEncaseSound(event.getLevel(), event.getPos());     
        };
        event.cancelWithResult(ItemInteractionResult.SUCCESS);
    };

    public static final BooleanProperty AXIS_ALONG_FIRST = DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE;

    public EncasedCrushingWheelControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS_ALONG_FIRST);
    };

    @Override
    public Block getCasing() {
        return AllBlocks.BRASS_CASING.get();
    };

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        boolean first = false;
        for (final Axis axis : Iterate.axes) {
            if (axis == state.getValue(CrushingWheelControllerBlock.FACING).getAxis()) continue;
            if (AllBlocks.CRUSHING_WHEEL.has(level.getBlockState(pos.relative(Direction.get(AxisDirection.POSITIVE, axis)))) && AllBlocks.CRUSHING_WHEEL.has(level.getBlockState(pos.relative(Direction.get(AxisDirection.NEGATIVE, axis))))) {
                first = true;
            };
            break;
        };
        level.setBlockAndUpdate(pos, defaultBlockState()
            .setValue(FACING, state.getValue(FACING))
            .setValue(VALID, state.getValue(VALID))
            .setValue(AXIS_ALONG_FIRST, first)
        );
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        context.getLevel().setBlockAndUpdate(context.getClickedPos(), AllBlocks.CRUSHING_WHEEL_CONTROLLER.getDefaultState()
            .setValue(FACING, state.getValue(FACING))
            .setValue(VALID, state.getValue(VALID))
        );
        return InteractionResult.SUCCESS;
    };

    @Override
    public BlockEntityType<? extends CrushingWheelControllerBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.ENCASED_CRUSHING_WHEEL_CONTROLLER.get();
    };
    
};
