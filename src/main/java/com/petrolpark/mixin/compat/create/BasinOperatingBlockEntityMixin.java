package com.petrolpark.mixin.compat.create;

import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.petrolpark.core.recipe.book.IRecipeBookAcceptorBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin extends KineticBlockEntity implements IRecipeBookAcceptorBlockEntity {

    @Shadow
    abstract Optional<BasinBlockEntity> getBasin();

    @Shadow
    abstract boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipeHolder);

    public BasinOperatingBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @Override
    public void addProxyRecipeBookAcceptorPositions(Consumer<BlockPos> posAdder) {
        getBasin().map(BlockEntity::getBlockPos).ifPresent(posAdder);
    };

    @Override
    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder) {
        return matchStaticFilters(recipeHolder);
    };
    
};
