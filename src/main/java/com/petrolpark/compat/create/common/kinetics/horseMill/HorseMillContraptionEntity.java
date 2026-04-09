package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.compat.create.PetrolparkCreateEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HorseMillContraptionEntity extends ControlledContraptionEntity {

    public HorseMillContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    };

    public static final HorseMillContraptionEntity create(Level world, IControlContraption controller, HorseMillContraption contraption) {
		final HorseMillContraptionEntity entity = new HorseMillContraptionEntity(PetrolparkCreateEntityTypes.HORSE_MILL_CONTRAPTION.get(), world);
		entity.controllerPos = controller.getBlockPosition();
		entity.setContraption(contraption);
		return entity;
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
		getContraption().recalculateSpeedAndStress();
	};

	@Override
	public boolean shouldRiderSit() {
		return false; // Mobs need to animate
	};

	@Override
	public void tick() {
		super.tick();
		final float contraptionAngle = getAngle(1f);
		for (Entity passenger : getPassengers()) {
			final BlockPos harnessPos = getContraption().getHarnessOf(passenger.getUUID());
			if (harnessPos == null) continue;
			final BlockState state = getContraption().getActorAt(harnessPos).getLeft().state();
			if (state.getBlock() instanceof HarnessBlock) {
				if (passenger instanceof LivingEntity living) living.calculateEntityAnimation(false);
				HarnessEntity.setFacing(passenger, -contraptionAngle - AngleHelper.horizontalAngle(state.getValue(HarnessBlock.FACING)));
			};
		};
	};

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		if (level().isClientSide()) return;
		
		getContraption().getHarnessMapping().remove(passenger.getUUID());
		CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new HorseMillContraptionHarnessMappingPacket(getId(), getContraption().getHarnessMapping()));
		getContraption().recalculateSpeedAndStress();
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
    
};
