package com.petrolpark.compat.create.common.kinetics.horseMill;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.compat.create.PetrolparkCreateEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HorseMillContraptionEntity extends ControlledContraptionEntity {

    public HorseMillContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    };

    public static final HorseMillContraptionEntity create(Level world, IControlContraption controller, Contraption contraption) {
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
	};

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		if (level().isClientSide()) return;
		
		getContraption().getHarnessMapping().remove(passenger.getUUID());
		CatnipServices.NETWORK.sendToClientsTrackingEntity(this, new HorseMillContraptionHarnessMappingPacket(getId(), getContraption().getHarnessMapping()));
	};

	@Override
	public void positionRider(Entity passenger, MoveFunction callback) {
		// TODO Auto-generated method stub
		super.positionRider(passenger, callback);
	};

	@Override
	public Vec3 getPassengerPosition(Entity passenger, float partialTicks) {
		// TODO Auto-generated method stub
		return super.getPassengerPosition(passenger, partialTicks);
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
