package petrolpark.mc.library.core.world.entity.player.team.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;

import net.createmod.catnip.platform.CatnipServices;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class ScoreboardTeamManager {

    protected final Map<PlayerTeam, ScoreboardTeam> teams = new HashMap<>();
    
    protected ScoreboardTeamSavedData savedData;

    public Optional<ITeam> get(Level level, String teamName) {
        PlayerTeam team = level.getScoreboard().getPlayerTeam(teamName);
        if (team == null) return Optional.empty();
        return Optional.of(teams.computeIfAbsent(team, t -> new ScoreboardTeam(level, t)));
    };

    public <T> void dataComponentChanged(Level level, ScoreboardTeam team, @Nonnull DataComponentPatch patch) {
        CatnipServices.PLATFORM.executeOnServerOnly(() -> () -> CatnipServices.NETWORK.sendToAllClients(new ScoreboardTeamComponentChangedPacket(team.team.getName(), patch)));
        markDirty();
    };

    public <T> void applyPatch(Level level, String teamName, DataComponentPatch patch) {
        get(level, teamName).ifPresent(team -> team.applyComponents(patch));
        markDirty();
    };

    @SubscribeEvent
    public void onPlayerLogIn(PlayerLoggedInEvent event) {
        Player player = event.getEntity();
		if (player instanceof ServerPlayer serverPlayer) {
			loadSavedData(serverPlayer.getServer());
			for (ScoreboardTeam team : teams.values()) {
                CatnipServices.NETWORK.sendToClient(serverPlayer, new ScoreboardTeamComponentChangedPacket(team.team.getName(), team.getDataComponentPatch()));
            };
		}
	};

    @SubscribeEvent
	public void onPlayerLogOut(PlayerLoggedOutEvent event) {};

    @SubscribeEvent
	public void onLoadLevel(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
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
            .computeIfAbsent(new SavedData.Factory<>(ScoreboardTeamSavedData::new, (tag, registries) -> load(server.overworld(), tag)), Petrolpark.MOD_ID + "_teams");
	};

    public void markDirty() {
        if (savedData != null) savedData.setDirty();
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
