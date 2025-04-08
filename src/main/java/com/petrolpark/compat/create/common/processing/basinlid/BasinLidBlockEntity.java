package com.petrolpark.compat.create.common.processing.basinlid;

import java.util.Optional;

import com.petrolpark.compat.create.core.block.entity.DirectlyAboveBasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BasinLidBlockEntity extends DirectlyAboveBasinOperatingBlockEntity {

    protected final Object recipeCacheKey = new Object();

    /**
     * <p>{@code -1} waiting for a matching Recipe
     * <p>{@code 0} apply the current Recipe
     * <p>{@code > 0} processing the recipe
     */
    public int processingTicksRemaining = -1;

    public BasinLidBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void tick() {
        super.tick();
        if (processingTicksRemaining > 0) {
            processingTicksRemaining--;
            if (processingTicksRemaining == 0) {
                applyBasinRecipe();
            };
            basinChecker.scheduleUpdate();
			return;
		};
    };

    @Override
    protected boolean isRunning() {
        return processingTicksRemaining > 0;
    };

    @Override
	public void startProcessingBasin() {
		if (processingTicksRemaining >= 0) return;
		super.startProcessingBasin();
		processingTicksRemaining = getCurrentProcessingRecipe().map(ProcessingRecipe::getProcessingDuration).orElse(400);
	};

	@Override
	protected void onBasinRemoved() {
		processingTicksRemaining = -1;
	};

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matchStaticFilters'");
    };

    @Override
    protected Object getRecipeCacheKey() {
        return recipeCacheKey;
    };

    @Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		processingTicksRemaining = compound.getInt("Ticks");
		super.read(compound, registries, clientPacket);
	};

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Ticks", processingTicksRemaining);
		super.write(compound, registries, clientPacket);
	};

    protected Optional<ProcessingRecipe<?>> getCurrentProcessingRecipe() {
        return currentRecipe instanceof ProcessingRecipe pr ? Optional.of(pr) : Optional.empty();
    };
    
};
