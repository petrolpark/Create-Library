package com.petrolpark.compat.create.core.block.composite;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.core.block.entity.IKineticBlockEntityDuck;
import com.petrolpark.util.NBTHelper;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CompositeKineticBlockEntity extends SmartBlockEntity {

    public CompositeKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    // Should not change in order
    public abstract List<CompositeKineticBlockEntityPart> getParts();

    @Override
    public void setLevel(@Nonnull Level level) {
        super.setLevel(level);
        getParts().forEach(part -> part.setLevel(level));
    };

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(@Nonnull BlockState blockState) {
        super.setBlockState(blockState);
        getParts().forEach(part -> part.setBlockState(blockState));
    };

    @Override
    public void initialize() {
        super.initialize();
        getParts().forEach(CompositeKineticBlockEntityPart::initialize);
    };

    @Override
    public void tick() {
        super.tick();
        getParts().forEach(CompositeKineticBlockEntityPart::tick);
    };

    @Override
    public void lazyTick() {
        super.lazyTick();
        getParts().forEach(CompositeKineticBlockEntityPart::lazyTick);
    };

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        getParts().forEach(CompositeKineticBlockEntityPart::onChunkUnloaded);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        getParts().forEach(CompositeKineticBlockEntityPart::invalidate);
    };

    @Override
    public void remove() {
        super.remove();
        getParts().forEach(CompositeKineticBlockEntityPart::remove);
    };

    @Override
    public void destroy() {
        super.destroy();
        getParts().forEach(CompositeKineticBlockEntityPart::destroy);
    };

    public void preventSpeedUpdates(int preventSpeedUpdate) {
        for (CompositeKineticBlockEntityPart part : getParts()) {
            part.preventSpeedUpdate = preventSpeedUpdate;
        };
    };

    /**
     * @see KineticBlock#updateIndirectNeighbourShapes(BlockState, net.minecraft.world.level.LevelAccessor, BlockPos, int, int)
     */
    public void removeExistingKineticInformation() {
        for (CompositeKineticBlockEntityPart part : getParts()) {
            if (part.preventSpeedUpdate > 0) return;
            // Remove previous information when block is added
            part.warnOfMovement();
            part.clearKineticInformation();
            part.updateSpeed = true;
        };
    };

    public void queueRotationIndicators() {
        getParts().forEach(CompositeKineticBlockEntityPart::queueRotationIndicators);
    };

    @Override
    protected void read(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        final ListTag partsTag = tag.getList("Parts", Tag.TAG_COMPOUND);
        for (int i = 0; i < getParts().size(); i++) {
            getParts().get(i).read(partsTag.getCompound(i), registries, clientPacket);
        };
    };

    @Override
    protected void write(CompoundTag tag, Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Parts", NBTHelper.writeCompoundList(getParts(), part -> {
            final CompoundTag partTag = new CompoundTag();
            part.write(partTag, registries, clientPacket);
            return partTag;
        }));
    };

    public abstract class CompositeKineticBlockEntityPart extends KineticBlockEntity {

        public CompositeKineticBlockEntityPart(BlockEntityType<?> typeIn) {
            super(typeIn, CompositeKineticBlockEntity.this.getBlockPos(), CompositeKineticBlockEntity.this.getBlockState());
            final Level level = CompositeKineticBlockEntity.this.getLevel();
            if (level != null) setLevel(level);
        };

        public abstract boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState state);

        public abstract int getIndex();

        @Override
        @Deprecated
        public final void setChanged() {
            CompositeKineticBlockEntity.this.setChanged();
        };
        
        @Override
        @Deprecated
        public final void sendData() {
            CompositeKineticBlockEntity.this.sendData();
        };

        @Override
        public void read(CompoundTag compound, Provider registries, boolean clientPacket) {
            super.read(compound, registries, clientPacket);
        };

        @Override
        public void write(CompoundTag compound, Provider registries, boolean clientPacket) {
            super.write(compound, registries, clientPacket);
        };

        @Override
        @Deprecated
        public final void addBehaviours(List<BlockEntityBehaviour> behaviours) {
            //NOOP
        };

        @Override
        @Deprecated
        public final void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
            //NOOP
        };

        @Override
        @Deprecated
        public final <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type) {
            return CompositeKineticBlockEntity.this.getBehaviour(type);
        };

        @Override
        @Deprecated
        public final Collection<BlockEntityBehaviour> getAllBehaviours() {
            return CompositeKineticBlockEntity.this.getAllBehaviours();
        };

        @Override
        @Deprecated
        public final void attachBehaviourLate(BlockEntityBehaviour behaviour) {
            CompositeKineticBlockEntity.this.attachBehaviourLate(behaviour);
        };

        @Override
        @Deprecated
        public final ItemRequirement getRequiredItems(BlockState state) {
            return ItemRequirement.NONE;
        };

        @Override
        @Deprecated
        public final void removeBehaviour(BehaviourType<?> type) {
            //NOOP
        };

        @Override
        public final void markVirtual() {
            //NOOP
        };

        @Override
        public final boolean isVirtual() {
            return CompositeKineticBlockEntity.this.isVirtual();
        };

        public final void queueRotationIndicators() {
            effects.queueRotationIndicators();
        };
    };

    /**
     * All copied from {@link GeneratingKineticBlockEntity}
     */
    public abstract class GeneratingCompositeKineticBlockEntityPart extends CompositeKineticBlockEntityPart {

        public boolean reActivateSource = false;

        public GeneratingCompositeKineticBlockEntityPart(BlockEntityType<?> typeIn) {
            super(typeIn);
        };

        public void notifyStressCapacityChange(float capacity) {
	        getOrCreateNetwork().updateCapacityFor(this, capacity);
	    };

        @Override
        public void removeSource() {
            if (hasSource() && isSource()) reActivateSource = true;
            super.removeSource();
        };

        @Override
        public void setSource(BlockPos source) {
            super.setSource(source);
            if (!reActivateSource) return;
            final Level level = getLevel();
            if (level == null) return;
            final KineticBlockEntity sourceBE = IKineticBlockEntityDuck.getSource(this);
            if (sourceBE == null) return;
            if (Math.abs(sourceBE.getSpeed()) >= Math.abs(getGeneratedSpeed())) reActivateSource = false;
        };

        @Override
        public void tick() {
            super.tick();
            if (reActivateSource) {
                updateGeneratedRotation();
                reActivateSource = false;
            };
        };

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
            if (!StressImpact.isEnabled()) return added;

            float stressBase = calculateAddedStressCapacity();
            // if (Mth.equal(stressBase, 0)) return added;

            // CreateLang.translate("gui.goggles.generator_stats")
            //     .forGoggles(tooltip);
            CreateLang.translate("tooltip.capacityProvided")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

            float speed = getTheoreticalSpeed();
            if (speed != getGeneratedSpeed() && speed != 0)
                stressBase *= getGeneratedSpeed() / speed;

            float stressTotal = Math.abs(stressBase * speed);

            CreateLang.number(stressTotal)
                .translate("generic.unit.stress")
                .style(ChatFormatting.AQUA)
                .space()
                .add(CreateLang.translate("gui.goggles.at_current_speed")
                    .style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

            return true;
        };

        public void updateGeneratedRotation() {
            final Level level = getLevel();
            final float speed = getGeneratedSpeed();
            final float prevSpeed = this.speed;

            if (level == null || level.isClientSide()) return;

            if (prevSpeed != speed) {
                if (!hasSource()) {
                    final SpeedLevel levelBefore = SpeedLevel.of(this.speed);
                    final SpeedLevel levelafter = SpeedLevel.of(speed);
                    if (levelBefore != levelafter) queueRotationIndicators();
                };

                applyNewSpeed(prevSpeed, speed);
            };

            if (hasNetwork() && speed != 0) {
                KineticNetwork network = getOrCreateNetwork();
                notifyStressCapacityChange(calculateAddedStressCapacity());
                getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
                network.updateStress();
            };

            onSpeedChanged(prevSpeed);
            sendData();
        };

        public void applyNewSpeed(float prevSpeed, float speed) {

            final Level level = getLevel();
            if (level == null) return;

            // Speed changed to 0
            if (speed == 0) {
                if (hasSource()) {
                    notifyStressCapacityChange(0);
                    getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
                    return;
                };
                detachKinetics();
                setSpeed(0);
                setNetwork(null);
                return;

            // Now turning - create a new Network
            } else if (prevSpeed == 0) {
                setSpeed(speed);
                setNetwork(createNetworkId());
                attachKinetics();
                return;

            // Change speed when overpowered by other generator
            } else if (hasSource()) {

                // Staying below Overpowered speed
                if (Math.abs(prevSpeed) >= Math.abs(speed)) {
                    if (Math.signum(prevSpeed) != Math.signum(speed)) level.destroyBlock(getBlockPos(), true);
                    return;
                };

                // Faster than attached network -> become the new source
                detachKinetics();
                setSpeed(speed);
                source = null;
                setNetwork(createNetworkId());
                attachKinetics();
                return;

            // Reapply source
            } else {
                detachKinetics();
                setSpeed(speed);
                attachKinetics();
            };
        };

        public long createNetworkId() {
            return getBlockPos().asLong() ^ ((long)getIndex() << 9l); // y coord only likely to take up first 9 bits
        };

    };

    public static final void addMultiParts(KineticBlockEntity from, BlockPos neighborPos, Consumer<KineticBlockEntity> beAdder) {
        final Level level = from.getLevel();
        if (level == null) return;
        final BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof IRotate) || !neighborState.hasBlockEntity() || !(level.getBlockEntity(neighborPos) instanceof CompositeKineticBlockEntity mpke)) return;
        mpke.getParts().forEach(part -> {
            if (RotationPropagator.isConnected(from, part) || RotationPropagator.isConnected(part, from)) beAdder.accept(part);
        });
    };
    
};
