package com.petrolpark.team.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.team.ITeam;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.PlayerTeam;

public class ScoreboardTeamManager {

    protected final Map<PlayerTeam, ScoreboardTeam> teams = new HashMap<>();
    
    protected ScoreboardTeamSavedData savedData;

    public Optional<ITeam> get(Level level, String teamName) {
        PlayerTeam team = level.getScoreboard().getPlayerTeam(teamName);
        if (team == null) return Optional.empty();
        return Optional.of(teams.computeIfAbsent(team, ScoreboardTeam::new));
    };

    public <T> void dataChanged(Level level, ScoreboardTeam team, DataComponentType<T> componentType) {
        T component = team.get(componentType);
        if (component != null) CatnipServices.PLATFORM.executeOnServerOnly(() -> () -> CatnipServices.NETWORK.sendToAllClients(new ScoreboardTeamComponentChangedPacket(team.team.getName(), new TypedDataComponent<>(componentType, component))));
        if (savedData != null) savedData.setDirty();
    };

    public <T> void setData(Level level, String teamName, TypedDataComponent<T> component) {
        get(level, teamName).ifPresent(team -> team.set(component.type(), component.value()));
    };

    public void playerLogin(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			loadSavedData(serverPlayer.getServer());
			for (ScoreboardTeam team : teams.values()) {
                team.getComponents().forEach(dt -> CatnipServices.NETWORK.sendToClient(serverPlayer, new ScoreboardTeamComponentChangedPacket(team.team.getName(), dt)));
            };
		}
	};

	public void playerLogout(Player player) {};

	public void levelLoaded(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		if (server == null || server.overworld() != level) return;
        teams.clear();
		savedData = null;
		loadSavedData(server);
	};

	private void loadSavedData(MinecraftServer server) {
		if (savedData != null) return;
		savedData = server.overworld()
            .getDataStorage()
            .computeIfAbsent(new SavedData.Factory<>(ScoreboardTeamSavedData::new, (tag, registries) -> load(server.overworld(), tag)), "petrolpark_teams");
	};

    public class ScoreboardTeamSavedData extends SavedData {

        @Override
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            for (ScoreboardTeam team : teams.values()) {
                tag.put(team.team.getName(), team.writeDataComponentsTag());
            };
            return tag;
        };
    
    };

    protected ScoreboardTeamSavedData load(Level overworld, CompoundTag tag) {
        ScoreboardTeamSavedData savedData = new ScoreboardTeamSavedData();

        for (String key : tag.getAllKeys()) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) continue;
            get(overworld, key).ifPresent(team -> team.applyComponents(DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, tag.get(key)).getOrThrow()));
        };

        return savedData;
    };
};
