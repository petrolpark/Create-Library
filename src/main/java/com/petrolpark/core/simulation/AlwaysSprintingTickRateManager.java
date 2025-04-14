package com.petrolpark.core.simulation;

import javax.annotation.Nonnull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerPlayer;

public class AlwaysSprintingTickRateManager extends ServerTickRateManager {

    protected final MinecraftServer server;

    public AlwaysSprintingTickRateManager(MinecraftServer server) {
        super(server);
        this.server = server;
    };

    @Override
    public boolean isSprinting() {
        return true;
    };

    @Override
    public void setFrozen(boolean frozen) {
        super.setFrozen(frozen);
    };

    @Override
    public boolean stopSprinting() {
        return false;
    };

    @Override
    public boolean requestGameToSprint(int sprintTime) {
        setFrozen(false);
        return true;
    };

    @Override
    public boolean checkShouldSprintThisTick() {
        return true;
    };

    @Override
    public void setTickRate(float tickRate) {
        super.setTickRate(tickRate);
        server.onTickRateChanged();
    };

    @Override
    public void updateJoiningPlayer(@Nonnull ServerPlayer player) {
        //NOOP
    };
    
};
