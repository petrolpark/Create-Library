package com.petrolpark.compat.create.shared.content.processing.centrifuge;

import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.compat.create.core.world.block.entity.behaviour.AdvancementBehaviour;
import com.petrolpark.core.world.item.crafting.recipeBook.IRecipeBookAcceptorBlockEntity;
import com.petrolpark.util.Lang;
import com.petrolpark.util.RecipeHelper;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CentrifugeBlockEntity extends KineticBlockEntity implements IRecipeBookAcceptorBlockEntity {

    protected FilteringBehaviour filter;
    protected final ItemStackHandler inventory = new ItemStackHandler(8);
    protected SmartFluidTankBehaviour inputTank, denseOutputTank, lightOutputTank;
    protected IFluidHandler verticalFluidCapability;
    protected IFluidHandler overallFluidCapability;

    protected int timer = -1;

    protected final Object recipeCacheKey = new Object();
    protected ICentrifugationRecipe lastRecipe;

    public CentrifugeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new AdvancementBehaviour(this));
        behaviours.add(filter = new FilteringBehaviour(this, new CentrifugeValueBox()).forRecipes());
        behaviours.add(inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, getEachTankCapacity(), true)
            .whenFluidUpdates(this::onFluidStackChanged));
        denseOutputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, getEachTankCapacity(), true)
            .forbidInsertion()
            .whenFluidUpdates(this::onFluidStackChanged);
        behaviours.add(lightOutputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, getEachTankCapacity(), true)
            .forbidInsertion()
            .whenFluidUpdates(this::onFluidStackChanged));
        verticalFluidCapability = new CombinedTankWrapper(inputTank.getCapability(), lightOutputTank.getCapability());
        overallFluidCapability = new CombinedTankWrapper(inputTank.getCapability(), denseOutputTank.getCapability(), lightOutputTank.getCapability());
    };

    @Override
    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder) {
        return recipeHolder.value() instanceof ICentrifugationRecipe;
    };

    public ItemStackHandler getItemHandler(Direction face) {
        return inventory;
    };

    public IFluidHandler getFluidHandler(Direction face) {
        if (face == null) return overallFluidCapability;
        if (face.getAxis().isVertical()) return verticalFluidCapability;
        return denseOutputTank.getCapability();
    };

    public int getEachTankCapacity() {
        return 2000; //TODO config
    };

    protected void onFluidStackChanged() {
        notifyUpdate();
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (getSpeed() == 0) return; // Don't do anything without rotational power
        if (isFull(denseOutputTank) || isFull(lightOutputTank)) return; // Don't do anything if output is full
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (getLevel().isClientSide()) {
                spawnParticles();
                return;
            };
            if (timer <= 0) {
                process();
                timer = 0;
            };
            sendData();
            return;
        } else if (timer < 0) {
            timer++;
            return;
        };

        if (lastRecipe == null || !lastRecipe.apply(this, true)) { // If the Recipe has changed
            final List<ICentrifugationRecipe> possibleRecipes = getMatchingRecipes();
            if (possibleRecipes.size() >= 1) {
                lastRecipe = possibleRecipes.get(0);
            } else { // If no recipe could be found
                lastRecipe = null;
            };
        };

        if (lastRecipe == null) {
            timer = -100; // If we have no Recipe, don't try checking again for another 100 ticks
        } else {
            timer = lastRecipe.getProcessingDuration();
        };

        sendData();
    };

    public FluidStack getInputStack() {
        return inputTank.getPrimaryHandler().getFluid();
    };

    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    };

    public void process() {
        if (lastRecipe == null) lastRecipe = getMatchingRecipes().stream().findFirst().orElse(null);
        if (lastRecipe != null) lastRecipe.apply(this, false);
    };

    public List<ICentrifugationRecipe> getMatchingRecipes() {
        return Stream.concat(
            RecipeFinder.get(recipeCacheKey, level, rh -> rh.value() instanceof ICentrifugationRecipe)
                .stream()
                .filter(rh -> RecipeHelper.isValidAt(rh, level, getBlockPos()))
                .map(rh -> rh.value() instanceof ICentrifugationRecipe r ? r : null),
            NeoForge.EVENT_BUS.post(new CentrifugationEvent(this)).recipes.stream()
        ).filter(r -> r.apply(this, true))
        .toList();
    };

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, getBlockPos(), inventory);
    };

    public boolean acceptOutputs(List<ItemStack> outputItems, FluidStack denseOutputFluid, FluidStack lightOutputFluid, boolean simulate) {
		denseOutputTank.allowInsertion();
        lightOutputTank.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputItems, denseOutputFluid, lightOutputFluid, simulate);
		denseOutputTank.forbidInsertion();
        lightOutputTank.forbidInsertion();
		return acceptOutputsInner;
	};

    private boolean acceptOutputsInner(List<ItemStack> outputItems, FluidStack denseOutputFluid, FluidStack lightOutputFluid, boolean simulate) {
		if (!acceptItemOutputsIntoCentrifuge(outputItems, simulate, inventory)) return false;
		if (!denseOutputFluid.isEmpty() && !acceptFluidOutputsIntoCentrifuge(denseOutputFluid, simulate, denseOutputTank.getCapability())) return false;
        if (!lightOutputFluid.isEmpty() && !acceptFluidOutputsIntoCentrifuge(lightOutputFluid, simulate, lightOutputTank.getCapability())) return false;
		return true;
	};

    private boolean acceptFluidOutputsIntoCentrifuge(FluidStack fluidStack, boolean simulate, IFluidHandler targetTank) {
        final FluidAction action = simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
        final int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
            ? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidStack.copy(), action)
            : targetTank.fill(fluidStack.copy(), action);
        if (fill != fluidStack.getAmount()) return false;
		return true;
	};

	private boolean acceptItemOutputsIntoCentrifuge(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
		for (ItemStack itemStack : outputItems) {
			if (!ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate).isEmpty()) return false;
		};
		return true;
	};

    public boolean isFull(SmartFluidTankBehaviour tank) {
        return tank.getPrimaryHandler().getCapacity() <= tank.getPrimaryHandler().getFluidAmount();
    };

    protected Vec3 particleOffset = Vec3.ZERO;

    @SuppressWarnings("null")
    public void spawnParticles() {
        final FluidStack fluidStack = inputTank.getPrimaryHandler().getFluid();
        if (fluidStack.isEmpty() || !hasLevel()) return;

        final RandomSource random = getLevel().getRandom();

        final ParticleOptions particleOptions = FluidFX.getFluidParticle(fluidStack);
        final float angle = random.nextFloat() * 360;
        Vec3 offset = particleOffset.add(0, 0, 0.7f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        final Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), random, 1 / 128f);
        getLevel().addParticle(particleOptions, center.x(), center.y(), center.z(), target.x(), target.y(), target.z());
    };

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        timer = compound.getInt("Time");
        inventory.deserializeNBT(registries, compound.getCompound("Items"));

        denseOutputTank.read(compound.getCompound("DenseOutputTank"), registries, clientPacket);
    };

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Time", timer);
        compound.put("Items", inventory.serializeNBT(registries));

        final CompoundTag denseOutputTag = new CompoundTag();
        denseOutputTank.write(denseOutputTag, registries, clientPacket);
        compound.put("DenseOutputTank", denseOutputTag);
    };

    @Override
    public void invalidate() {
        invalidateCapabilities();
        super.invalidate();
    };

    @Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		Lang.builder().translate("gui.goggles.centrifuge")
			.forGoggles(tooltip);
            
		boolean innerChamberEmpty = inputTank.isEmpty() && lightOutputTank.isEmpty();

        for (int slot = 0; slot < inventory.getSlots(); slot++) if (!inventory.getStackInSlot(slot).isEmpty()) {
            innerChamberEmpty = false;
            break;
        };

        if (!innerChamberEmpty) Lang.builder().translate("gui.goggles.centrifuge.inner")
            .forGoggles(tooltip);

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stackInSlot = inventory.getStackInSlot(slot);
			if (stackInSlot.isEmpty()) continue;
			CreateLang.text("")
				.add(Component.translatable(stackInSlot.getDescriptionId()).withStyle(ChatFormatting.GRAY))
				.add(CreateLang.text(" x" + stackInSlot.getCount()).style(ChatFormatting.GREEN))
				.forGoggles(tooltip, 1);
		};

        if (!inputTank.isEmpty()) addFluidToTooltip(getInputStack(), tooltip);
        if (!lightOutputTank.isEmpty()) addFluidToTooltip(lightOutputTank.getPrimaryHandler().getFluid(), tooltip);

        if (!denseOutputTank.isEmpty()) {
            Lang.builder().translate("gui.goggles.centrifuge.outer")
                .forGoggles(tooltip);
            addFluidToTooltip(denseOutputTank.getPrimaryHandler().getFluid(), tooltip);
        };

		if (innerChamberEmpty && denseOutputTank.isEmpty()) {
            tooltip.remove(0);
        } else {
            tooltip.add(Component.literal(""));
        };

		return (!innerChamberEmpty || !denseOutputTank.isEmpty()) | super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	};

    protected void addFluidToTooltip(FluidStack fluidStack, List<Component> tooltip) {
        CreateLang.text("")
            .add(CreateLang.fluidName(fluidStack)
                .add(CreateLang.text(" "))
                .style(ChatFormatting.GRAY)
                .add(CreateLang.number(fluidStack.getAmount())
                    .add(CreateLang.translate("generic.unit.millibuckets"))
                    .style(ChatFormatting.BLUE)))
            .forGoggles(tooltip, 1);
    };

    static class CentrifugeValueBox extends ValueBoxTransform.Sided {

        @Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8d, 14d, 16.05d);
		};

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal() && !state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction));
        };

    };
};
