package com.petrolpark.client.ponder.instruction;

import java.util.UUID;

import com.petrolpark.client.ponder.PonderPlayer;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.instruction.PonderInstruction;

import net.minecraft.world.entity.projectile.FishingHook;

public class CreateFishingHookInstruction extends PonderInstruction {

    protected final ElementLink<EntityElement> playerElementLink;
    protected final ElementLink<EntityElement> hookElementLink;

    protected CreateFishingHookInstruction(ElementLink<EntityElement> playerElementLink) {
        this.playerElementLink = playerElementLink;
        hookElementLink = new ElementLink<>(EntityElement.class, UUID.randomUUID());
    };

    public static ElementLink<EntityElement> add(SceneBuilder scene, ElementLink<EntityElement> player) {
        CreateFishingHookInstruction instruction = new CreateFishingHookInstruction(player);
        scene.addInstruction(instruction);
        return instruction.hookElementLink;
    };

    @Override
    public boolean isComplete() {
        return true;
    };

    @Override
    public void tick(PonderScene scene) {
        scene.resolve(playerElementLink).ifPresent(entity -> {
            if (!(entity instanceof PonderPlayer player)) return;
            PonderWorld world = scene.getWorld();
            FishingHook hook = new FishingHook(player, world, 0, 0);
            EntityElement handle = new EntityElement(hook);
            scene.addElement(handle);
            scene.linkElement(handle, hookElementLink);
            world.addFreshEntity(hook);
        });
    };
    
};
