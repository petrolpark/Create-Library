package com.petrolpark.core.item.decay.drying.rack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.item.wooden.WoodenBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DryingRackBlockEntity extends WoodenBlockEntity {

    public final ItemStackHandler inv = new ItemStackHandler() {

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemDecay.checkDecay(super.getStackInSlot(slot));
        };

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            final ItemStack decayingStack = IApplyDecayRecipe.withAppliedDecay(level, PetrolparkRecipeTypes.DRYING.get(), stack.copy(), !simulate);
            if (ItemStack.isSameItemSameComponents(stack, decayingStack)) return stack;
            return stack.copyWithCount(super.insertItem(slot, decayingStack, simulate).getCount());
        };

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return IApplyDecayRecipe.withAppliedDecayRemoved(level, PetrolparkRecipeTypes.DRYING.get(), super.extractItem(slot, amount, simulate));
        };

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        };

        @Override
        protected void onContentsChanged(int slot) {
            notifyUpdate();
        };
    };

    public DryingRackBlockEntity(BlockEntityType<DryingRackBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };

    @SuppressWarnings("null")
    public void dropContents() {
        if (level != null) Containers.dropContents(level, getBlockPos(), NonNullList.of(ItemStack.EMPTY, inv.extractItem(0, 1, false)));
    };

    public ItemStackHandler getItemHandler(@Nullable Direction face) {
        return inv;
    };

    @Override
    protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inv.deserializeNBT(registries, tag.getCompound("Inventory"));
    };

    @Override
    protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inv.serializeNBT(registries));
    };
    
};
