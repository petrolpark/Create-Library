package com.petrolpark.core.world.block.soil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.core.world.block.entity.BlockEntityBase;
import com.petrolpark.core.world.fluid.InputOnlyFluidTank;
import com.petrolpark.shared.registry.SharedBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

@ApiStatus.Experimental
@EventBusSubscriber
public class SoilBlockEntity extends BlockEntityBase {

    public static final int getMaxHydration() {
        return 1000;
    };

    public final ItemStackHandler fertilizer = new ItemStackHandler(1) {

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        };

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        };
    };

    public final HydrationFluidHandler hydration = new HydrationFluidHandler();

    public SoilBlockEntity(BlockEntityType<SoilBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    };

    public class HydrationFluidHandler extends InputOnlyFluidTank {

        protected @Nullable Direction lastInputDirection = null;

        public HydrationFluidHandler() {
            super(getMaxHydration());
        };

        @Override
        public boolean isFluidValid(@Nonnull FluidStack stack) {
            return stack.is(FluidTags.WATER);
        };

        @Override
        public void onFluidChanged() {
            setChanged();
        };

        public HydrationFluidHandler setInputDirection(@Nullable Direction direction) {
            lastInputDirection = direction;
            return this;
        };
    };

    public ItemStackHandler getItemHandler(Direction face) {
        return fertilizer;
    };

    public InputOnlyFluidTank getFluidHandler(Direction face) {
        return hydration.setInputDirection(face);
    };

    @SubscribeEvent
    public static final void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SharedBlockEntityTypes.SOIL.get(), SoilBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SharedBlockEntityTypes.SOIL.get(), SoilBlockEntity::getFluidHandler);
    };
    
};
