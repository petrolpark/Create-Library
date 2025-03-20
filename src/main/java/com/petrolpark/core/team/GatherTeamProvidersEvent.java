package com.petrolpark.core.team;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.petrolpark.core.team.scoreboard.ScoreboardTeam;
import com.petrolpark.core.team.singleplayer.SinglePlayerTeam;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Fired to gather {@link ITeam}s of which this Player {@link ITeam#isMember(Player) is a part}.
 */
public class GatherTeamProvidersEvent extends PlayerEvent {

    protected final List<ITeam.Provider> teamProviders;

    public GatherTeamProvidersEvent(Player player) {
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

    public boolean remove(ITeam.Provider teamProvider) {
        return teamProviders.remove(teamProvider);
    };
    
};
