package com.petrolpark.tube;

import java.util.function.Consumer;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.CreateBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

@RequiresCreate
public class TubeStructuralBlock extends Block implements IBE<TubeStructuralBlockEntity> {

    public TubeStructuralBlock(Properties properties) {
        super(properties);
    };

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level pLevel, BlockState pState, BlockEntityType<S> pBlockEntityType) {
        return null; // Doesn't need to tick
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pIsMoving) {
        IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, pIsMoving);
    };

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    };

    @Override
    public Class<TubeStructuralBlockEntity> getBlockEntityClass() {
        return TubeStructuralBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TubeStructuralBlockEntity> getBlockEntityType() {
        return CreateBlockEntityTypes.TUBE_STRUCTURE.get();
    };

    @OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
		consumer.accept(new RenderProperties());
	};

    public static class RenderProperties implements IClientBlockExtensions {

        @Override
        public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
            return true;
        };
    };
    
};
