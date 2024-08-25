package com.petrolpark.client.ponder.instruction;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.instruction.TickingInstruction;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.util.Mth;

public class CameraShakeInstruction extends TickingInstruction {

    private float originalXRotation;

    public CameraShakeInstruction() {
        super(false, 40);
        originalXRotation = 0f;
    };

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        originalXRotation = scene.getTransform().xRotation.getChaseTarget();
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        scene.getTransform().xRotation.chase(originalXRotation + 20f * Mth.sin(remainingTicks * 10f) * Math.exp(0.2f * (remainingTicks - totalTicks)), 1f, Chaser.EXP);
    };
    
};
