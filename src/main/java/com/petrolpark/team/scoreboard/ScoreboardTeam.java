package com.petrolpark.team.scoreboard;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.Petrolpark;
import com.petrolpark.team.AbstractTeam;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.NoTeam;
import com.petrolpark.team.PetrolparkTeamProviderTypes;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

public class ScoreboardTeam extends AbstractTeam {

    public final PlayerTeam team;

    public static final ITeam.Provider provider(PlayerTeam team) {
        return new ScoreboardTeam.Provider(team.getName());
    };

    public ScoreboardTeam(PlayerTeam team) {
        this(team, DataComponentPatch.EMPTY);
    };

    protected ScoreboardTeam(PlayerTeam team, DataComponentPatch components) {
        super(components);
        this.team = team;
    };

    @Override
    public ITeam.Provider getProvider() {
        return provider(team);
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

    public static record Provider(String teamName) implements ITeam.Provider {

        public static final MapCodec<Provider> CODEC = NetworkHelper.singleFieldMapCodec(Codec.STRING, "team", Provider::teamName, Provider::new);
        public static final StreamCodec<FriendlyByteBuf, Provider> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Provider::teamName, Provider::new);

        @Override
        public ITeam provideTeam(Level level) {
            return Petrolpark.SCOREBOARD_TEAMS.get(level, teamName).orElse(NoTeam.INSTANCE);
        };

        @Override
        public ProviderType getProviderType() {
            return PetrolparkTeamProviderTypes.SCOREBOARD.get();
        };

    };
    
};
