package com.petrolpark.compat.create.common.kinetics.horseMill;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

import net.minecraft.core.BlockPos;

public class HarnessMovementBehaviour implements MovementBehaviour {
    
    @Override
    public void startMoving(MovementContext context) {
        MovementBehaviour.super.startMoving(context);
        final int indexOf = context.contraption instanceof HorseMillContraption horseMillContraption ? horseMillContraption.getHarnesses().indexOf(context.localPos) : -1;
        context.data.putInt("HarnessIndex", indexOf);
    };

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        MovementBehaviour.super.visitNewPosition(context, pos);

		if (!(context.contraption.entity instanceof HorseMillContraptionEntity contraptionEntity)) return;
		final int index = context.data.getInt("HarnessIndex");
		if (index == -1) return;

        //TODO check walkable
    };
};
