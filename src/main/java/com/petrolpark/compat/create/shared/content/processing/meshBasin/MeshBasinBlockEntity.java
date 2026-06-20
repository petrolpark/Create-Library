package com.petrolpark.compat.create.shared.content.processing.meshBasin;

import java.util.List;
import java.util.Optional;

import com.petrolpark.compat.create.core.data.recipe.AdvancedBasinRecipe;
import com.petrolpark.compat.create.core.world.block.entity.basin.AdvancedBasinOperatingBlockEntity;
import com.petrolpark.compat.create.core.world.block.entity.basin.IDifferentBasinBlockEntity;
import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.petrolpark.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlockEntity;
import com.petrolpark.mixin.compat.create.accessor.BasinBlockEntityAccessor;
import com.petrolpark.mixin.compat.create.accessor.BasinOperatingBlockEntityAccessor;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.item.SmartInventory;

import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class MeshBasinBlockEntity extends BasinBlockEntity implements IDifferentBasinBlockEntity, IRecipeBookAcceptorBlockEntity {

    protected int selfProcessingTicksRemaining = -1;
    protected Recipe<?> currentSelfRecipe = null;
    protected Object selfRecipeCacheKey = new Object();

    public MeshBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };
    
    public IItemHandlerModifiable getItemHandler(Direction direction) {
        return itemCapability;
    };

    public IFluidHandler getFluidHandler(Direction direction) {
        return fluidCapability;
    };

    protected List<IntAttached<ItemStack>> getVisualizedOutputItems() {
        return ((BasinBlockEntityAccessor)this).getVisualziedOutputItems();
    };

    @Override
    public void tick() {
        final Level level = getLevel();

        if (level != null && level.isClientSide() && isSelfRunning()) renderParticles();

        if (getBasinOperator().isPresent() && !(level instanceof PonderLevel)) {
            selfProcessingTicksRemaining = -1;
            currentSelfRecipe = null;
        };

        boolean hadContentsChanged = haveContentsChanged();

        super.tick();

        if (level == null) return;
        if (selfProcessingTicksRemaining > 0) {
            if (selfProcessingTicksRemaining % 20 == 0) level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.75f, 0.5f);
            selfProcessingTicksRemaining--;
            if (selfProcessingTicksRemaining == 0) applySelfRecipe();
			return;
        };

        if (hadContentsChanged && getBasinOperator().isEmpty()) updateSelfRecipe();
    };

    public boolean isSelfRunning() {
        return selfProcessingTicksRemaining > 0;
    };

    protected boolean updateSelfRecipe() {
		if (isSelfRunning() || getBasinOperator().isPresent()) return true;
        final Level level = getLevel();
		if (level == null || level.isClientSide()) return true;
		if (!canContinueProcessing()) return true;

		final List<Recipe<?>> recipes = getMatchingSelfRecipes();
		if (recipes.isEmpty()) return true;
		currentSelfRecipe = recipes.get(0);
        resetProcessingTime();
		sendData();
		return true;
	};

    protected void applySelfRecipe() {
		if (currentSelfRecipe == null) return;

		boolean wasEmpty = canContinueProcessing();
		if (!BasinRecipe.apply(this, currentSelfRecipe)) return;
		inputTank.sendDataImmediately();

		// Continue mixing
		if (wasEmpty && BasinRecipe.match(this, currentSelfRecipe)) continueWithPreviousRecipe();
		
        sendData();
		notifyChangeOfContents();
	};

    public boolean continueWithPreviousRecipe() {
        resetProcessingTime();
        return true;
    };

    public void resetProcessingTime() {
        selfProcessingTicksRemaining = Math.max(1, getCurrentSelfProcessingRecipe().map(ProcessingRecipe::getProcessingDuration).orElse(200));
        //bubbling = getCurrentLiddedBasinRecipe().map(LiddedBasinRecipe::bubbles).orElse(false);
    };

    public List<Recipe<?>> getMatchingSelfRecipes() {
        return AdvancedBasinOperatingBlockEntity.getMatchingRecipes(this, selfRecipeCacheKey, r -> BasinRecipe.match(this, r), this::matchStaticFiltersForSelfProcessing);
    };

    protected Optional<ProcessingRecipe<?, ?>> getCurrentSelfProcessingRecipe() {
        return currentSelfRecipe instanceof ProcessingRecipe pr ? Optional.of(pr) : Optional.empty();
    };

    public boolean matchStaticFiltersForSelfProcessing(RecipeHolder<?> rh) {
        return rh.value().getType() == SharedCreateRecipeTypes.BOILING.getType();
    };

    public void renderParticles() {
		for (final SmartInventory inv : getInvs()) {
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getItem(slot);
				if (stackInSlot.isEmpty()) continue;
				ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
				spillParticle(data);
			};
		};

		for (final SmartFluidTankBehaviour behaviour : getTanks()) {
			if (behaviour == null) continue;
			for (TankSegment tankSegment : behaviour.getTanks()) {
				if (tankSegment.isEmpty(0)) continue;
				spillParticle(FluidFX.getFluidParticle(tankSegment.getRenderedFluid()));
			};
		};
	};

	protected void spillParticle(ParticleOptions data) {
        final Level level = getLevel();
        if (level == null)  return;
        if (level.getRandom().nextFloat() < 0.625f) return;
		final float angle = level.getRandom().nextFloat() * 360f;
		Vec3 offset = new Vec3(0f, 0f, 0.25f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		final Vec3 center = offset.add(VecHelper.getCenterOf(getBlockPos()));
		Vec3 motion = VecHelper.offsetRandomly(offset.scale(0.5f), level.getRandom(), 1 / 128f);
		level.addParticle(data, center.x, center.y + 0.25f, center.z, motion.x, motion.y + 0.2f, motion.z);
	};

    @Override
    protected void read(CompoundTag compound, Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        selfProcessingTicksRemaining = compound.getInt("ProcessingTicksRemaining");
    };

    @Override
    public void write(CompoundTag compound, Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("ProcessingTicksRemaining", selfProcessingTicksRemaining);
    };

    @Override
    public boolean matchStaticFilters(Recipe<?> recipe) {
        return recipe instanceof AdvancedBasinRecipe advancedRecipe && advancedRecipe.isForMeshBasin();
    };

    @Override
    public void onAvailableRecipesChanged() {
        selfRecipeCacheKey = new Object();
        updateSelfRecipe();
    };

    @Override
    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder) {
        return getBasinOperator().map(be -> ((BasinOperatingBlockEntityAccessor)be).callMatchStaticFilters(recipeHolder))
            .orElse(matchStaticFiltersForSelfProcessing(recipeHolder));
    };
    
};
