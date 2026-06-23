package petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateEntityTypes;

public class HorseMillContraptionEntity extends ControlledContraptionEntity {

	public static final EntityDataAccessor<Float> GENERATED_SPEED = SynchedEntityData.defineId(HorseMillContraptionEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> GENERATED_STRESS_CAPACITY = SynchedEntityData.defineId(HorseMillContraptionEntity.class, EntityDataSerializers.FLOAT);

    public HorseMillContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    };

    public static final HorseMillContraptionEntity create(Level world, IControlContraption controller, HorseMillContraption contraption) {
		final HorseMillContraptionEntity entity = new HorseMillContraptionEntity(SharedCreateEntityTypes.HORSE_MILL_CONTRAPTION.get(), world);
		entity.controllerPos = controller.getBlockPosition();
		entity.setContraption(contraption);
		return entity;
	};

	@Override
	protected void defineSynchedData(Builder builder) {
		super.defineSynchedData(builder);
		builder
			.define(GENERATED_SPEED, 0f)
			.define(GENERATED_STRESS_CAPACITY, 0f);
	};

	public void calculateGeneratedSpeedAndStress() {
		if (!(level() instanceof ServerLevel level)) return;
		boolean hasEntities = false;
		float speed = 16f;
		float stressCapacity = 0f;
		for (Entity passenger : getPassengers()) {
			final BlockPos harnessPos = getContraption().getHarnessOf(passenger.getUUID());
			if (harnessPos == null) continue;
			Optional<HorseMillProperties> propertiesOp = HorseMillProperties.get(passenger);
			if (propertiesOp.isEmpty()) continue;
			hasEntities = true;
			final LootContext lootContext = new LootContext.Builder(new LootParams.Builder(level)
				.withParameter(LootContextParams.THIS_ENTITY, passenger)
				.withParameter(LootContextParams.ORIGIN, passenger.position())
				.create(LootContextParamSets.ADVANCEMENT_ENTITY)
			).create(Optional.empty());
			speed = Mth.clamp(42.16f * propertiesOp.get().maxSpeed().getFloat(passenger, lootContext) / Mth.sqrt((float)harnessPos.distSqr(Vec3i.ZERO)), 0f, speed);
			stressCapacity += propertiesOp.get().stressCapacity().getFloat(passenger, lootContext);
		};
		if (hasEntities && speed > 0f) {
			stressCapacity /= speed;
			if (getContraption().clockwise != null && !getContraption().clockwise) speed *= -1f;
		} else {
			speed = stressCapacity = 0f;
		};
		getEntityData().set(GENERATED_SPEED, speed);
		getEntityData().set(GENERATED_STRESS_CAPACITY, stressCapacity);
		if (getController() instanceof GeneratingKineticBlockEntity kbe) kbe.updateGeneratedRotation();
	};

	public void addHarnessedPassenger(Entity passenger, int harnessIndex) {
		// // Not necessary I think as passenger should never be a Player
		// for (Entity entity : getPassengers()) {
		// 	final BlockPos harnessOf = getContraption().getHarnessOf(entity.getUUID());
		// 	if (harnessOf != null && harnessOf.equals(getContraption().getHarnesses().get(harnessIndex))) {
		// 		if (entity instanceof Player) return;
		// 		if (!(passenger instanceof Player)) return;
		// 		entity.stopRiding();
		// 	};
		// };
		passenger.startRiding(this, true);
		if (level().isClientSide()) return;

		getContraption().getHarnessMapping().put(passenger.getUUID(), harnessIndex);
		CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new HorseMillContraptionHarnessMappingPacket(getId(), getContraption().getHarnessMapping()));
		calculateGeneratedSpeedAndStress();
	};

	@Override
	public boolean shouldRiderSit() {
		return false; // Mobs need to animate
	};

	@Override
	public void tick() {
		boolean wasStalled = isStalled();

		super.tick();

		// Face passengers the right way
		final float contraptionAngle = getAngle(1f);
		for (Entity passenger : getPassengers()) {
			final BlockPos harnessPos = getContraption().getHarnessOf(passenger.getUUID());
			if (harnessPos == null) continue;
			final BlockState state = getContraption().getActorAt(harnessPos).getLeft().state();
			if (state.getBlock() instanceof HarnessBlock) {
				HarnessEntity.setFacing(passenger, -contraptionAngle - AngleHelper.horizontalAngle(state.getValue(HarnessBlock.FACING)));
			};
		};

		// Update generated rotation if we have stopped or started
		if (wasStalled != isStalled() && getController() instanceof GeneratingKineticBlockEntity kbe) kbe.updateGeneratedRotation();
	};

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		if (level().isClientSide()) return;
		
		getContraption().getHarnessMapping().remove(passenger.getUUID());
		CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new HorseMillContraptionHarnessMappingPacket(getId(), getContraption().getHarnessMapping()));
		calculateGeneratedSpeedAndStress();
	};

	@Override
	public void positionRider(Entity passenger, MoveFunction callback) {
		if (contraption != null && getContraption().getHarnessOf(passenger.getUUID()) != null) {
			final Vec3 pos = getPassengerPosition(passenger, 1f);
			if (pos != null) {
				callback.accept(passenger, pos.x(), pos.y(), pos.z());
				return;
			};
		};
		super.positionRider(passenger, callback);
	};

	@Override
	public Vec3 getPassengerPosition(Entity passenger, float partialTicks) {
		final Vec3 seatedPos = super.getPassengerPosition(passenger, partialTicks);
		if (seatedPos != null) return seatedPos;
		if (contraption == null) return null;

		final BlockPos harnessPos = getContraption().getHarnessOf(passenger.getUUID());
		if (harnessPos == null) return null;
		final BlockState harnessState = getContraption().getActorAt(harnessPos).getLeft().state();
		if (!(harnessState.getBlock() instanceof HarnessBlock)) return null;

		final Optional<Vec3> offset = HorseMillProperties.get(passenger).map(HorseMillProperties::positionOffset);
		if (offset.isPresent()) return toGlobalVector(Vec3.atBottomCenterOf(harnessPos), partialTicks)
			.add(VecHelper.rotate(offset.get(), getAngle(partialTicks) + AngleHelper.horizontalAngle(harnessState.getValue(HarnessBlock.FACING)), Axis.Y)); //TODO
	
		return null;
	};

	@Override
	protected boolean canAddPassenger(@NotNull Entity passenger) {
		return super.canAddPassenger(passenger) || (HarnessBlock.canBeHarnessed(passenger) && getContraption().getHarnessMapping().size() < getContraption().getHarnesses().size());
	};

	@Override
	public HorseMillContraption getContraption() {
		return (HorseMillContraption)super.getContraption();
	};

	@Override
	protected void setContraption(Contraption contraption) {
		if (!(contraption instanceof HorseMillContraption)) throw new IllegalArgumentException("Must be a Horse Mill Contraption");
		super.setContraption(contraption);
	};

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		getEntityData().set(GENERATED_SPEED, compound.getFloat("generated_speed"));
		getEntityData().set(GENERATED_STRESS_CAPACITY, compound.getFloat("generated_stress_capacity"));
	};

	@Override
	protected void writeAdditional(CompoundTag compound, Provider registries, boolean spawnPacket) {
		super.writeAdditional(compound, registries, spawnPacket);
		compound.putFloat("generated_speed", getEntityData().get(GENERATED_SPEED));
		compound.putFloat("generated_stress_capacity", getEntityData().get(GENERATED_STRESS_CAPACITY));
	};

	public static final void onEntityTickPost(EntityTickEvent.Post event) {
		// This is cancelled in ServerLevel and ClientLevel if the entity is on a Horse Mill Contraption, so we need to do it now
		if (event.getEntity().isPassenger() && event.getEntity().getVehicle() instanceof HorseMillContraptionEntity) event.getEntity().setOldPosAndRot();
	};
    
};
