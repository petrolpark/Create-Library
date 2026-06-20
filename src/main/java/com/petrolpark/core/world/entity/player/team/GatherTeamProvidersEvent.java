package com.petrolpark.core.world.entity.player.team;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.petrolpark.core.world.entity.player.team.scoreboard.ScoreboardTeam;
import com.petrolpark.core.world.entity.player.team.singleplayer.SinglePlayerTeam;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Fired to gather {@link ITeam}s of which this Player {@link ITeam#isMember(Player) is a part}.
 * You should <em>not</em> fire this manually. Instead call {@link ITeam#streamAll(Player)}.
 * You <em>should</em> subscribe to this (on the {@link NeoForge#EVENT_BUS main Event Bus}) to add custom {@link ITeam} implementations.
 */
public class GatherTeamProvidersEvent extends PlayerEvent {

    protected final List<ITeam.Provider> teamProviders;

    protected GatherTeamProvidersEvent(Player player) {
        super(player);
        teamProviders = new ArrayList<>(2);

        // Built-in Teams Providers
        add(SinglePlayerTeam.provider(player));
        PlayerTeam team = player.getTeam();
        if (team != null) add(ScoreboardTeam.provider(team));
    };

    public List<ITeam.Provider> getTeamProvidersUnmodifiable() {
        return ImmutableList.copyOf(teamProviders);
    };

    public List<ITeam> getTeamsUnmodifiable(Level level) {
        return teamProviders.stream().map(provider -> provider.provideTeam(level)).toList();
    };

    public boolean add(ITeam.Provider teamProvider) {
        return teamProviders.add(teamProvider);
    };
    
};
