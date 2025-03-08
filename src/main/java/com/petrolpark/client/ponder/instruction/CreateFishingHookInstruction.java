package com.petrolpark.client.ponder.instruction;

import java.util.UUID;

import com.petrolpark.client.ponder.PonderPlayer;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.ElementLinkImpl;
import net.createmod.ponder.foundation.element.EntityElementImpl;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.world.entity.projectile.FishingHook;

public class CreateFishingHookInstruction extends PonderInstruction {

    protected final ElementLink<EntityElement> playerElementLink;
    protected final ElementLink<EntityElement> hookElementLink;

    protected CreateFishingHookInstruction(ElementLink<EntityElement> playerElementLink) {
        this.playerElementLink = playerElementLink;
        hookElementLink = new ElementLinkImpl<>(EntityElement.class, UUID.randomUUID());
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
        EntityElement element = scene.resolve(playerElementLink);
        if (element != null) element.ifPresent(entity -> {
            if (!(entity instanceof PonderPlayer player)) return;
            PonderLevel world = scene.getWorld();
            FishingHook hook = new FishingHook(player, world, 0, 0);
            EntityElement handle = new EntityElementImpl(hook);
            scene.addElement(handle);
            scene.linkElement(handle, hookElementLink);
            world.addFreshEntity(hook);
        });
    };
    
};
