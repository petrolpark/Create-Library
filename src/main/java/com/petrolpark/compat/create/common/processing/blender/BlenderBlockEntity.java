package com.petrolpark.compat.create.common.processing.blender;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateDamageSources;
import com.petrolpark.compat.create.PetrolparkCreateFluids;
import com.petrolpark.compat.create.PetrolparkCreateRecipeTypes;
import com.petrolpark.compat.create.PetrolparkCreateBlockEntityTypes;
import com.petrolpark.compat.create.core.block.entity.basin.BelowBasinOperatingBlockEntity;
import com.petrolpark.core.world.entity.EntityFallOnEvent;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.item.SmartInventory;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class BlenderBlockEntity extends BelowBasinOperatingBlockEntity {

    protected Object recipeCacheKey = new Object();

    /**
     * <p>{@code -1} waiting for a matching Recipe
     * <p>{@code 0} apply the current Recipe
     * <p>{@code > 0} processing the recipe
     */
    public int processingTicksRemaining = -1;

    protected List<WeakReference<LivingEntity>> hurtingEntities = new LinkedList<>();

    public BlenderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        final Level level = getLevel();
        if (level == null) return;

        if (level.isClientSide() && isRunning()) renderParticles();

        final BlockState aboveState = level.getBlockState(getBlockPos().above());

        if (!level.isClientSide() && !hurtingEntities.isEmpty() && getSpeed() != 0f && (aboveState.isAir() || getBasin().isPresent())) {
            final Iterator<WeakReference<LivingEntity>> iterator = hurtingEntities.iterator();
            final DamageSource damageSource = PetrolparkCreateDamageSources.blender(level);
            float damage = Mth.clamp(Mth.abs(getSpeed()) / 64f, 0.125f, 5f);
            while (iterator.hasNext()) {
                final LivingEntity entity = iterator.next().get();
                if (entity == null || !canHurt(entity)) {
                    iterator.remove();
                    continue;
                };
                if (entity.hurt(damageSource, damage) && SharedFeatureFlag.BLOOD.enabled() && !PetrolparkTags.EntityTypes.DOESNT_BLEED.matches(entity)) getBasin()
                    .flatMap(be -> Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, null)))
                    .ifPresent(handler -> handler.fill(new FluidStack(PetrolparkCreateFluids.BLOOD.get(), (int)(damage * 5)), FluidAction.EXECUTE));
            };
        };

        if (processingTicksRemaining > 0) {
            if (processingTicksRemaining % 20 == 0) level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.75f, 0.5f);
            processingTicksRemaining--;
            if (processingTicksRemaining == 0) {
                applyBasinRecipe();
                basinChecker.scheduleUpdate();
            };
			return;
		};
    };

    public static final void onEntityFallOn(EntityFallOnEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof LivingEntity livingEntity && AllBlocks.BASIN.has(event.getLevel().getBlockState(livingEntity.blockPosition()))) {
            event.getLevel().getBlockEntity(event.getPos(), PetrolparkCreateBlockEntityTypes.BLENDER.get()).ifPresent(be -> be.addHurtingEntity(livingEntity));
        };
    };

    public void addHurtingEntity(LivingEntity livingEntity) {
        if (canHurt(livingEntity)) hurtingEntities.add(new WeakReference<>(livingEntity));
    };

    public boolean canHurt(LivingEntity entity) {
        return entity.getOnPos().equals(getBlockPos())
            || (entity.blockPosition().equals(getBlockPos().above()) && getBasin().isPresent());
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
        processingTicksRemaining = getCurrentProcessingRecipe().map(ProcessingRecipe::getProcessingDuration).orElse(200);
    };

	@Override
	protected void onBasinRemoved() {
		processingTicksRemaining = -1;
	};

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value().getType() == PetrolparkCreateRecipeTypes.BLENDING.getType();
    };

    @Override
    protected Object getRecipeCacheKey() {
        return recipeCacheKey;
    };

    @Override
    public void updateRecipeCacheKey() {
        recipeCacheKey = new Object();
    };

    /**
     * Copied from {@link MechanicalMixerBlockEntity#renderParticles() Create source code}
     */
    public void renderParticles() {
		final Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent() || level == null) return;

		for (SmartInventory inv : basin.get().getInvs()) {
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getItem(slot);
				if (stackInSlot.isEmpty()) continue;
				ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
				spillParticle(data);
			};
		};

		for (SmartFluidTankBehaviour behaviour : basin.get().getTanks()) {
			if (behaviour == null) continue;
			for (TankSegment tankSegment : behaviour.getTanks()) {
				if (tankSegment.isEmpty(0)) continue;
				spillParticle(FluidFX.getFluidParticle(tankSegment.getRenderedFluid()));
			};
		};
	};

    /**
     * Copied from {@link MechanicalMixerBlockEntity#spillParticle Create source code}
     * @param data
     */
    @SuppressWarnings("null")
	protected void spillParticle(ParticleOptions data) {
		final float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y).add(0, .25f, 0);
		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y - 1.75f, center.z, target.x, target.y, target.z);
	};

    @Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		processingTicksRemaining = compound.getInt("Ticks");
		super.read(compound, registries, clientPacket);

        if (clientPacket && hasLevel()) getBasin().ifPresent(be -> be.setAreFluidsMoving(isRunning()));
	};

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Ticks", processingTicksRemaining);
		super.write(compound, registries, clientPacket);
	};
    
};
