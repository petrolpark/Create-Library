package com.petrolpark.mixin;

import javax.annotation.Nonnull;

import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.core.item.decay.ageing.AgeingContainerWrapper;
import com.petrolpark.core.item.decay.ageing.AgeingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Wrap methods for inserting/removing Items from Barrels to allow them to do {@link AgeingRecipe}s.
 */
@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends RandomizableContainerBlockEntity {

    protected BarrelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        throw new AssertionError();
    };

    @Override
    public ItemStack getItem(int index) {
        return AgeingContainerWrapper.getItem(level, super::getItem, index);
    };

    @Override
    public ItemStack removeItem(int index, int count) {
        return AgeingContainerWrapper.removeItem(level, super::removeItem, index, count);
    };

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return AgeingContainerWrapper.removeItemNoUpdate(level, super::removeItemNoUpdate, index);
    };

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        AgeingContainerWrapper.setItem(level, super::setItem, index, stack);
    };
    
};
