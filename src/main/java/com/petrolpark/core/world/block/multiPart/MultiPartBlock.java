package com.petrolpark.core.world.block.multiPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;
import com.petrolpark.core.world.block.multiPart.MultiPartBlock.IPart;
import com.petrolpark.mixin.accessor.BlockAccessor;
import com.petrolpark.util.RayHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;

@ParametersAreNonnullByDefault
public abstract class MultiPartBlock<PART extends IPart> extends Block {

    protected final Map<BlockState, VoxelShape> shapeCache;
    protected final Map<BlockState, Clipper<PART>> clipperCache;

    public MultiPartBlock(BlockBehaviour.Properties properties) {
        super(properties);
        shapeCache = getShapeForEachState(state -> getParts(state).stream().map(IPart::shape).reduce(Shapes.empty(), Shapes::or));
        clipperCache = stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), state -> Clipper.of(getParts(state))));
    };

    public abstract Collection<PART> getParts(BlockState state);

    public abstract BlockState withoutPart(BlockState state, PART part);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            final Entity entity = entityContext.getEntity();
            if (entity != null) {
                final PART part = clipperCache.get(state).clip(entity);
                if (part != null) return part.shape();
            };
        };
        return getFullShape(state, level, pos);
    };

    public VoxelShape getFullShape(BlockState state, BlockGetter level, BlockPos pos) {
        return shapeCache.get(state);
    };

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getFullShape(state, level, pos);
    };

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getFullShape(state, level, pos);
    };

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getFullShape(state, level, pos);
    };

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        final PART part = clipperCache.get(state).clip(player);
        if (part != null) {
            level.setBlock(pos, withoutPart(state, part), 11);
            if (willHarvest) { // Actual Block breaking is cancelled, so do it here
                player.awardStat(Stats.BLOCK_MINED.get(this));
                player.causeFoodExhaustion(0.05f);
                dropPartResources(part, state, level, pos, level.getBlockEntity(pos), player, player.getMainHandItem());
            };
            return false;
        };
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    };

    @SuppressWarnings("null")
    public void dropPartResources(PART part, BlockState state, Level level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        if (level instanceof ServerLevel serverLevel) {
            BlockAccessor.callBeginCapturingDrops();
            getPartDrops(part, state, serverLevel, pos, blockEntity, entity, tool).forEach(stack -> popResource(level, pos, stack));
            final List<ItemEntity> captured = BlockAccessor.callStopCapturingDrops();
            CommonHooks.handleBlockDrops(serverLevel, pos, state, blockEntity, captured, entity, tool);
        };
    };

    public List<ItemStack> getPartDrops(PART part, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        final LootParams.Builder params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.BLOCK_STATE, state)
            .withParameter(LootContextParams.TOOL, tool)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        return getPartDrops(part, params);
    };

    public List<ItemStack> getPartDrops(PART part, LootParams.Builder paramsBuilder) {
        if (part.loot() == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams params = paramsBuilder.create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = params.getLevel();
            LootTable table = serverlevel.getServer().reloadableRegistries().getLootTable(part.loot());
            return table.getRandomItems(params);
        }
    };

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        final List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));
        params = params.withParameter(LootContextParams.BLOCK_STATE, state);
        for (PART part : getParts(state)) drops.addAll(getPartDrops(part, params));
        return drops;
    };

    public interface IPart {
        public VoxelShape shape();
        public ResourceKey<LootTable> loot();
    };

    public record Clipper<PART extends IPart>(List<AABB> boxes, List<PART> parts) {

        @Nullable
        public PART clip(Entity entity) {
            final int index = RayHelper.getHit(boxes(), entity);
            if (index >= 0) return parts.get(index);
            return null;
        };
        
        public static final <PART extends IPart> Clipper<PART> of(Collection<PART> uniqueParts) {
            final List<AABB> boxes = new ArrayList<>();
            final List<PART> parts = new ArrayList<>();
            for (PART part : uniqueParts) {
                for (AABB box : part.shape().toAabbs()) {
                    boxes.add(box);
                    parts.add(part);
                };
            };
            return new Clipper<>(boxes, parts);
        };
    };
    
};
