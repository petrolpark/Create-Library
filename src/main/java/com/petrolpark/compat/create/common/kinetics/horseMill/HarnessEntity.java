package com.petrolpark.compat.create.common.kinetics.horseMill;

import com.petrolpark.compat.create.PetrolparkCreateEntityTypes;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;

import net.createmod.catnip.math.AngleHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
        final BlockState state = level().getBlockState(blockPosition());
        if (state.getBlock() instanceof HarnessBlock) {
            final float angle = AngleHelper.horizontalAngle(state.getValue(HarnessBlock.FACING));
            for (Entity entity : getPassengers()) {
                if (!(entity instanceof LivingEntity living)) continue;
                if (level().isClientSide()) {
                    living.lerpTo(0, 0, 0, 0, 0, 0);
                    living.lerpHeadTo(0, 0);
                    living.setYRot(angle);
                    living.setXRot(0);
                    living.yBodyRot = angle;
                    living.yHeadRot = angle;
                } else {
                    living.setYRot(angle);
                };
            };
            if (isVehicle()) return;
        };
		discard();
    };
    
};
