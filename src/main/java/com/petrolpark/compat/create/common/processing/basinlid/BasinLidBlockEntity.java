package com.petrolpark.compat.create.common.processing.basinlid;

import java.util.Optional;

import com.petrolpark.PetrolparkParticleTypes;
import com.petrolpark.compat.create.CreateRecipeTypes;
import com.petrolpark.compat.create.core.block.entity.basin.DirectlyAboveBasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BasinLidBlockEntity extends DirectlyAboveBasinOperatingBlockEntity {

    protected Object recipeCacheKey = new Object();

    /**
     * <p>{@code -1} waiting for a matching Recipe
     * <p>{@code 0} apply the current Recipe
     * <p>{@code > 0} processing the recipe
     */
    public int processingTicksRemaining = -1;

    public boolean bubbling;

    public BasinLidBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void tick() {
        super.tick();
        final Level level = getLevel();
        if (level != null && processingTicksRemaining > 0) {
            if (level.isClientSide()) renderParticles();
            if (processingTicksRemaining % 20 == 0) level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.75f, 0.5f);
            processingTicksRemaining--;
            if (processingTicksRemaining == 0) {
                applyBasinRecipe();
                basinChecker.scheduleUpdate();
            };
			return;
		};
    };

    @Override
    protected boolean isRunning() {
        return processingTicksRemaining > 0;
    };

    @Override
	public void startProcessingBasin() {
		if (processingTicksRemaining > 0) return;
		super.startProcessingBasin();
		resetProcessingTime();
	};

    @Override
    public boolean continueWithPreviousRecipe() {
        resetProcessingTime();
        return true;
    };

    public void resetProcessingTime() {
        processingTicksRemaining = getCurrentProcessingRecipe().map(ProcessingRecipe::getProcessingDuration).orElse(400);
        bubbling = getCurrentLiddedBasinRecipe().map(LiddedBasinRecipe::bubbles).orElse(false);
    };

	@Override
	protected void onBasinRemoved() {
		processingTicksRemaining = -1;
	};

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == CreateRecipeTypes.LIDDED_BASIN.getType();
    };

    @Override
    protected Object getRecipeCacheKey() {
        return recipeCacheKey;
    };

    @Override
    public void updateRecipeCacheKey() {
        recipeCacheKey = new Object();
    };

    public static final Vec3 PARTICLE_OFFSET = new Vec3(-5 / 16d, 9 / 16d, 0d);

    @OnlyIn(Dist.CLIENT)
    public void renderParticles() {
        Level level = getLevel();
        if (level == null || !bubbling || processingTicksRemaining % 5 != 0 || level.getRandom().nextInt(5) != 0) return;
        Vec3 loc = VecHelper.getCenterOf(getBlockPos()).add(VecHelper.rotate(PARTICLE_OFFSET, AngleHelper.horizontalAngle(getBlockState().getValue(BasinLidBlock.FACING)), Axis.Y));
        level.addParticle(PetrolparkParticleTypes.AIR_BUBBLE.get(), loc.x(), loc.y(), loc.z(), 0d, 0.2d, 0d);
    };

    @Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		processingTicksRemaining = compound.getInt("Ticks");
        bubbling = compound.getBoolean("Bubbling");
		super.read(compound, registries, clientPacket);
	};

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Ticks", processingTicksRemaining);
        compound.putBoolean("Bubbling", bubbling);
		super.write(compound, registries, clientPacket);
	};

    protected Optional<LiddedBasinRecipe> getCurrentLiddedBasinRecipe() {
        return getCurrentProcessingRecipe().map(r -> r instanceof LiddedBasinRecipe lbr ? lbr : null);
    };
    
};
