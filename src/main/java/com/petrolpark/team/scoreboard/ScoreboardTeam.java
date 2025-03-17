package com.petrolpark.team.scoreboard;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.Petrolpark;
import com.petrolpark.team.AbstractTeam;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.NoTeam;
import com.petrolpark.team.PetrolparkTeamProviderTypes;
import com.petrolpark.util.CodecHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ScoreboardTeam extends AbstractTeam {

    public final Level level;
    public final PlayerTeam team;

    public static final ITeam.Provider provider(PlayerTeam team) {
        return new ScoreboardTeam.Provider(team.getName());
    };

    public ScoreboardTeam(Level level, PlayerTeam team) {
        this(level, team, DataComponentPatch.EMPTY);
    };

    protected ScoreboardTeam(Level level, PlayerTeam team, DataComponentPatch components) {
        super(components);
        this.level = level;
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
    public Stream<String> streamMemberUsernames() {
        return team.getPlayers().stream();
    };

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    public Stream<Player> streamMembers() {
        MinecraftServer server = level.getServer();
        if (server != null) return streamMemberUsernames().map(server.getPlayerList()::getPlayerByName);
        return Stream.empty();
    };

    @Override
    public boolean isAdmin(Player player) {
        return player.hasPermissions(2);
    };

    @Override
    public Component getName() {
        return team.getDisplayName();
    };

    @Override
    public void setChanged(DataComponentPatch patch) {
        Petrolpark.SCOREBOARD_TEAMS.dataComponentChanged(level, this, patch);
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        //TODO
    };

    public static record Provider(String teamName) implements ITeam.Provider {

        public static final MapCodec<Provider> CODEC = CodecHelper.singleFieldMap(Codec.STRING, "team", Provider::teamName, Provider::new);
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
