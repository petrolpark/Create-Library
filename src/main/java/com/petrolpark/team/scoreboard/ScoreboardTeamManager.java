package com.petrolpark.team.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.team.scoreboard.ScoreboardTeamManager.ScoreboardTeamSavedData;
import com.simibubi.create.foundation.utility.DistExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
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
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.api.distmarker.Dist;

public class ScoreboardTeamManager {

    protected final Map<PlayerTeam, ScoreboardTeam> teams = new HashMap<>();
    
    protected ScoreboardTeamSavedData savedData;

    public Optional<ITeam> get(Level level, String teamName) {
        PlayerTeam team = level.getScoreboard().getPlayerTeam(teamName);
        if (team == null) return Optional.empty();
        return Optional.of(teams.computeIfAbsent(team, ScoreboardTeam::new));
    };

    public void dataChanged(Level level, ScoreboardTeam team, ITeamDataType<?> dataType) {
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> PetrolparkMessages.sendToAllClients(new ScoreboardTeamDataChangedPacket(level, team, dataType)));
        if (savedData != null) savedData.setDirty();
    };

    public void setData(Level level, String teamName, ITeamDataType<?> dataType, CompoundTag dataTag) {
        get(level, teamName).ifPresent(team -> team.loadTeamData(level, dataTag, dataType));
    };

    public void playerLogin(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			loadSavedData(serverPlayer.getServer());
			for (ScoreboardTeam team : teams.values()) {
                team.streamNonBlankTeamData().forEach(dt -> PetrolparkMessages.sendToClient(new ScoreboardTeamDataChangedPacket(serverPlayer.level(), team, dt), serverPlayer));
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
