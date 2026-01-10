package com.petrolpark.core.item.decay.drying.rack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.PetrolparkBlockEntityTypes;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.item.wooden.WoodenBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

@EventBusSubscriber
public class DryingRackBlockEntity extends WoodenBlockEntity {

    public final ItemStackHandler inv = new ItemStackHandler() {

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            final ItemStack decayingStack = IApplyDecayRecipe.withAgeingDecay(level, PetrolparkRecipeTypes.DRYING.get(), stack, !simulate);
            if (ItemStack.isSameItemSameComponents(stack, decayingStack)) return stack;
            return super.insertItem(slot, decayingStack, simulate);
        };

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return IApplyDecayRecipe.withAgeingDecayRemoved(level, PetrolparkRecipeTypes.DRYING.get(), super.extractItem(slot, amount, simulate));
        };

        @Override
        public int getSlotLimit(int slot) {
            return 1;
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

    @SubscribeEvent
    public static final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        if (SharedFeatureFlag.DRYING_RACK.enabled()) event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PetrolparkBlockEntityTypes.DRYING_RACK.get(), DryingRackBlockEntity::getItemHandler);
    };
    
};
