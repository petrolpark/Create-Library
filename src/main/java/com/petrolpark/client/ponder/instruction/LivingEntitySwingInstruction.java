package com.petrolpark.client.ponder.instruction;

import java.util.function.Consumer;

import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.instruction.TickingInstruction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntitySwingInstruction extends TickingInstruction {

    protected final ElementLink<EntityElement> entityLink;
    protected final Consumer<LivingEntity> onApexReached;

    public LivingEntitySwingInstruction(ElementLink<EntityElement> entityLink) {
        this(entityLink, e -> {});
    };
    
    public LivingEntitySwingInstruction(ElementLink<EntityElement> entityLink, Consumer<LivingEntity> onApexReached) {
        super(false, 7);
        this.entityLink = entityLink;
        this.onApexReached = onApexReached;
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        scene.resolve(entityLink).ifPresent(e -> {
            if (!(e instanceof LivingEntity entity)) return;
            entity.swingingArm = InteractionHand.MAIN_HAND;
            if (remainingTicks == 0) {
                entity.attackAnim = 0f;
                return;
            } else if (remainingTicks == 4) onApexReached.accept(entity);
            entity.attackAnim = (float)(totalTicks - remainingTicks) / 6f;
        });
    };
};
