package com.petrolpark.compat.create.common.kinetics.horseMill;

import com.petrolpark.compat.create.PetrolparkCreateEntityTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;

import net.createmod.catnip.math.AngleHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HarnessEntity extends SeatEntity {

    public HarnessEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    };

    public HarnessEntity(Level level) {
        this(PetrolparkCreateEntityTypes.HARNESS.get(), level);
        noPhysics = true;
    };

    @Override
    public void tick() {
        if (level().isClientSide()) return;
        final BlockState state = level().getBlockState(blockPosition());
        final float angle = AngleHelper.horizontalAngle(getDirection());
        for (Entity passenger : getPassengers()) {
            passenger.setYRot(angle);
            passenger.setYBodyRot(angle);
        };
		if (isVehicle() && state.getBlock() instanceof HarnessBlock) return;
		discard();
    };
    
};
