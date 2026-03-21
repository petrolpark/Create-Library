package com.petrolpark.compat.create.common.processing.crushingWheel;

import java.util.List;
import java.util.Optional;

import com.petrolpark.util.RandomHelper;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class EncasedCrushingWheelControllerBlockEntity extends CrushingWheelControllerBlockEntity {

    private static final Object crushingRecipesKey = new Object();
    private static final Object millingRecipesKey = new Object();

    protected FilteringBehaviour filtering;
    private final RecipeWrapper recipeWrapper; // Duplicate of private field

    public EncasedCrushingWheelControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        recipeWrapper = new RecipeWrapper(inventory);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(filtering = new FilteringBehaviour(this, new CrushingWheelValueBox()));
    };

    @Override
    public Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> findRecipe() {
        final Level level = getLevel();
        if (level == null) return Optional.empty();
        List<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> recipes = getRecipesOfType(crushingRecipesKey, level, AllRecipeTypes.CRUSHING.getType());
        if (recipes.isEmpty()) recipes = getRecipesOfType(millingRecipesKey, level, AllRecipeTypes.MILLING.getType());
        if (!recipes.isEmpty()) return Optional.of(RandomHelper.pick(level.getRandom(), recipes));
        return Optional.empty();
    };
    
    @SuppressWarnings("unchecked")
    public List<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> getRecipesOfType(Object cacheKey, Level level, RecipeType<?> recipeType) {
        return RecipeFinder.get(cacheKey, level, RecipeConditions.isOfType(recipeType))
            .stream()
            .map(rh -> new RecipeHolder<>(rh.id(), (StandardProcessingRecipe<RecipeWrapper>)rh.value()))
            .filter(rh -> rh.value().matches(recipeWrapper, level))
            .filter(rh -> rh.value().getRollableResults().stream().map(ProcessingOutput::getStack).anyMatch(filtering::test))
            .toList();
    };

    public class CrushingWheelValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8d, 8d, 15.15d);
        };

        @Override
        public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
            return isSideActive(state, getSide());
        };

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            final Axis facingAxis = getBlockState().getValue(EncasedCrushingWheelControllerBlock.FACING).getAxis();
            if (direction.getAxis() == facingAxis) return false;
            for (Axis axis : Iterate.axes) {
                if (axis == facingAxis) continue;
                return getBlockState().getValue(EncasedCrushingWheelControllerBlock.AXIS_ALONG_FIRST) == (direction.getAxis() != axis);
            };
            return false; // Dead
        };

    };
    
};
