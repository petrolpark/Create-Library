package com.petrolpark.compat.create.shared.content.processing.mandrel;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import com.petrolpark.shared.SharedFeatureFlag;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

//TODO
public class MandrelBlockEntity extends KineticBlockEntity {

    public final ItemStackHandler inputInv;
    public final ItemStackHandler outputInv;
    public final IItemHandler capability;
    public int timer = 100;
    protected MandrelRecipe lastRecipe = null;

    public boolean running = false;
    protected float animationStartPartialTick = 0f;

    public MandrelBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inputInv = new ItemStackHandler(1);
        outputInv = new ItemStackHandler(1);
        capability = new InventoryHandler();
    };

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (SharedFeatureFlag.MANDREL.enabled())
		    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SharedCreateBlockEntityTypes.MANDREL.get(), (be, context) -> be.capability);
	};

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
    };

    @Override
    public void tick() {
        super.tick();
        Level level = getLevel();
        if (level == null) return;

        if (getSpeed() == 0f) return;

        if (timer > 0) {
            timer--;
            if (level.isClientSide()) {
                spawnParticles();
                return;
            };
            if (timer <= 0) process();
            return;
        };

        SingleRecipeInput recipeInput = new SingleRecipeInput(inputInv.getStackInSlot(0));
        if (lastRecipe == null || !lastRecipe.matches(recipeInput, level)) {
            Optional<RecipeHolder<MandrelRecipe>> recipe = level.getRecipeManager().getRecipeFor(SharedCreateRecipeTypes.MANDREL.getType(), recipeInput, level);
			if (!recipe.isPresent()) {
				timer = 100; // Wait a few seconds to try again
				sendData();
			} else {
				lastRecipe = recipe.get().value();
				startProcessing();
                sendData();
			};
            return;
        };

        startProcessing();
        sendData();
    };

    public void startProcessing() {
        if (lastRecipe == null) return;
        timer = (int)((2f + lastRecipe.animation().getTotalAngleSubtended(this) / 360f) / (getSpeed() / 1200));
        running = true;
        sendData();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::logAnimationStartTime);
    };

    public void resetProcessing() {
        running = false;
        timer = 100;
    };

    public void spawnParticles() {

    };

    @Override
	public void invalidate() {
		super.invalidate();
		invalidateCapabilities();
	};

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(getLevel(), getBlockPos(), inputInv);
		ItemHelper.dropContents(getLevel(), getBlockPos(), outputInv);
	};

    /*
     * Temporary, copied from Millstone
     */
    public void process() {
        running = false;

        SingleRecipeInput inventoryIn = new SingleRecipeInput(inputInv.getStackInSlot(0));
        Level level = getLevel();
        if (level == null) return;

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RecipeHolder<MandrelRecipe>> recipe = level.getRecipeManager().getRecipeFor(SharedCreateRecipeTypes.MANDREL.getType(), inventoryIn, level);
			if (!recipe.isPresent()) return;
			lastRecipe = recipe.get().value();
		};

		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		stackInSlot.shrink(1);
		inputInv.setStackInSlot(0, stackInSlot);
		ItemHandlerHelper.insertItemStacked(outputInv, lastRecipe.result().copy(), false);

		sendData();
		setChanged();
    };

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("null")
    protected void logAnimationStartTime() {
        animationStartPartialTick = AnimationTickHolder.getRenderTime(level);
    };

    protected boolean canProcess(ItemStack stack) {
        Level level = getLevel();
        if (level == null) return false;
        SingleRecipeInput input = new SingleRecipeInput(stack);
		if (lastRecipe != null && lastRecipe.matches(input, level)) return true;
		return level.getRecipeManager().getRecipeFor(SharedCreateRecipeTypes.MANDREL.getType(), input, level).isPresent();
	};

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        resetProcessing();
    };

    protected class InventoryHandler extends CombinedInvWrapper {

		public InventoryHandler() {
			super(inputInv, outputInv);
		};

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot))) return false;
			return canProcess(stack) && super.isItemValid(slot, stack);
		};

		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot))) return stack;
			if (!isItemValid(slot, stack)) return stack;
			return super.insertItem(slot, stack, simulate);
		};

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot))) return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		};

	};
    
};
