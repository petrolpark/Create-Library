package com.petrolpark.mixin.compat.create;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.create.core.block.entity.DirectlyAboveBasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin extends SmartBlockEntity {
    
    public BasinBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @WrapMethod(
        method = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;getOperator()Ljava/util/Optional;",
        remap = false
    )
    @SuppressWarnings("null")
    public Optional<BasinOperatingBlockEntity> wrapGetOperator(Operation<Optional<BasinOperatingBlockEntity>> operation) {
        return operation.call().or(() -> level.getBlockEntity(getBlockPos().above()) instanceof DirectlyAboveBasinOperatingBlockEntity bobe ? Optional.of(bobe) : Optional.empty());
    };
};
