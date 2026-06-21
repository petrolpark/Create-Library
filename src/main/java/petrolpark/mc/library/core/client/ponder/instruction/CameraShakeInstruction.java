package petrolpark.mc.library.core.client.ponder.instruction;

import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
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
