package com.petrolpark.client.ponder.instruction;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;

import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;

public class AdvanceTimeOfDayInstruction extends TickingInstruction {

    public final int speed;

    public AdvanceTimeOfDayInstruction(int ticks, int speed) {
        super(false, ticks);
        this.speed = speed;
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        ClientLevelData levelData = (ClientLevelData)scene.getWorld().getLevelData();
        levelData.setDayTime(levelData.getDayTime() + speed);
    };
    
};
