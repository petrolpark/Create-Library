package com.petrolpark.mixin.compat.create;

import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.world.block.entity.basin.BelowBasinOperatingBlockEntity;
import com.petrolpark.compat.create.core.world.block.entity.basin.DirectlyAboveBasinOperatingBlockEntity;
import com.petrolpark.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlockEntity;
import com.petrolpark.mixin.compat.create.accessor.BasinOperatingBlockEntityAccessor;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin extends SmartBlockEntity implements IRecipeBookAcceptorBlockEntity {
    
    @Shadow
    abstract Optional<BasinOperatingBlockEntity> getOperator();

    public BasinBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @WrapMethod(
        method = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;getOperator()Ljava/util/Optional;",
        remap = false
    )
    @SuppressWarnings("null")
    public Optional<BasinOperatingBlockEntity> petrolpark$getOtherOperators(Operation<Optional<BasinOperatingBlockEntity>> operation) {
        return operation.call()
            .or(() -> level.getBlockEntity(getBlockPos().above()) instanceof DirectlyAboveBasinOperatingBlockEntity bobe ? Optional.of(bobe) : Optional.empty())
            .or(() -> level.getBlockEntity(getBlockPos().below()) instanceof BelowBasinOperatingBlockEntity bboe ? Optional.of(bboe) : Optional.empty());
    };

    @Override
    public void addProxyRecipeBookAcceptorPositions(Consumer<BlockPos> posAdder) {
        getOperator().map(BlockEntity::getBlockPos).ifPresent(posAdder);
    };

    @Override
    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder) {
        return getOperator().map(be -> ((BasinOperatingBlockEntityAccessor)be).callMatchStaticFilters(recipeHolder)).orElse(recipeHolder.value() instanceof BasinRecipe);
    };
};
