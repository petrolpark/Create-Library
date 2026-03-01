package com.petrolpark.compat.create.common.processing.extrusion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.compat.ISharedFeature;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateDamageSources;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.core.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ExtrusionDieBlock extends RotatedPillarBlock implements IBE<ExtrusionDieBlockEntity>, IWrenchable, ISharedFeature {

    public static final VoxelShaper SHAPE = new AllShapes.Builder(Block.box(0, 0, 7, 16, 16, 9)).forDirectional(Direction.SOUTH);

    public ExtrusionDieBlock(Properties properties) {
        super(properties);
    };

    @Nullable
	@Override
	public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level pLevel, BlockState pState, BlockEntityType<S> pBlockEntityType) {
		return null;
	};

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.get(state.getValue(AXIS));
    };

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        AbstractRememberPlacerBehaviour.setPlacedBy(level, pos, placer);
        super.setPlacedBy(level, pos, state, placer, stack);
    };

    /**
     * Copied from the {@link net.minecraft.world.level.block.SweetBerryBushBlock#entityInside Minecraft source code}.
     */
    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));
            if (!level.isClientSide() && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d0 = Math.abs(entity.getX() - entity.xOld);
                double d1 = Math.abs(entity.getZ() - entity.zOld);
                if (d0 >= (double)0.003f || d1 >= (double)0.003f) {
                    entity.hurt(PetrolparkCreateDamageSources.extrusionDie(level), 3f);
                };
            };
        };
    }

    @Override
    public Class<ExtrusionDieBlockEntity> getBlockEntityClass() {
        return ExtrusionDieBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends ExtrusionDieBlockEntity> getBlockEntityType() {
        return PetrolparkCreateBlockEntityTypes.EXTRUSION_DIE.get();
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.EXTRUSION;
    };

};
