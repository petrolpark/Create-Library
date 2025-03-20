package com.petrolpark.core.team.packet;

import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.team.ITeam.Provider;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public abstract class BindTeamPacket implements ServerboundPacketPayload {

    public final ITeam.Provider teamProvider;

    public BindTeamPacket(Provider teamProvider) {
        this.teamProvider = teamProvider;
    };

    public ITeam.Provider getTeamProvider() {
        return teamProvider;
    };

    public abstract void handle(ITeam.Provider teamProvider, ServerPlayer player);

    @Override
    public final void handle(ServerPlayer player) {
        ITeam team = teamProvider.provideTeam(player.level());
        if (team.isMember(player)) handle(teamProvider, player);
    };

    @FunctionalInterface
    public static interface Factory {
        public BindTeamPacket create(ITeam.Provider teamProvider);
    };
    
};
