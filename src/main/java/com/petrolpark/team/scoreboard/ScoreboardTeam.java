package com.petrolpark.team.scoreboard;

import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.team.AbstractTeam;
import com.petrolpark.team.TeamTypes;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

public class ScoreboardTeam extends AbstractTeam<ScoreboardTeam> {

    public final PlayerTeam team;

    public ScoreboardTeam(PlayerTeam team) {
        this.team = team;
    };

    @Override
    public ITeamType<ScoreboardTeam> getType() {
        return TeamTypes.SCOREBOARD.get();
    };

    @Override
    public boolean isMember(Player player) {
        return player.getTeam() == team;
    };

    @Override
    public int memberCount() {
        return team.getPlayers().size();
    };

    @Override
    public Stream<String> streamMemberUsernames(Level level) {
        return team.getPlayers().stream();
    };

    @Override
    public boolean isAdmin(Player player) {
        return player.hasPermissions(2);
    };

    @Override
    public Component getName(Level level) {
        return team.getDisplayName();
    };

    @Override
    public void setChanged(Level level, ITeamDataType<?> dataType) {
        Petrolpark.SCOREBOARD_TEAMS.dataChanged(level, this, dataType);
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        //TODO
    };

    public static final class Type implements ITeamType<ScoreboardTeam> {

        @Override
        public ScoreboardTeam read(CompoundTag tag, Level level) {
            return Petrolpark.SCOREBOARD_TEAMS.get(tag.getString("Team")).orElse(null);
        };

        @Override
        public void write(ScoreboardTeam team, CompoundTag tag) {
            tag.putString("Team", team.team.getName());
        };

    };
    
};
