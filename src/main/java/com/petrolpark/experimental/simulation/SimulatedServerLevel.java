package com.petrolpark.experimental.simulation;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;

@ApiStatus.Experimental
public class SimulatedServerLevel extends ServerLevel {

    public SimulatedServerLevel(MinecraftServer server, Executor dispatcher, LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData, ResourceKey<Level> dimension, LevelStem levelStem,
            ChunkProgressListener progressListener, long biomeZoomSeed) {
        super(
            server,
            dispatcher,
            levelStorageAccess,
            serverLevelData,
            dimension,
            levelStem,
            progressListener,
            false,
            biomeZoomSeed,
            Collections.emptyList(), // No spawning
            true,
            null
        );
        this.getBiomeManager();
    };

    @Override
	public List<ServerPlayer> players() {
		return Collections.emptyList();
	};

	@Override
	public void playSound(@Nullable Player player, double x, double y, double z, @Nonnull SoundEvent soundEvent, @Nonnull SoundSource soundSource, float volume, float pitch) {};

	@Override
	public void playSound(@Nullable Player player, @Nonnull Entity entity, @Nonnull SoundEvent soundEvent, @Nonnull SoundSource soundSource, float volume, float pitch) {};

	@Override
	public Entity getEntity(int id) {
		return null;
	};

	@Override
	public @Nullable MapItemSavedData getMapData(@Nonnull MapId mapId) {
		return null;
	};

	@Override
	public void setMapData(@Nonnull MapId mapId, @Nonnull MapItemSavedData mapData) {}

	@Override
	public MapId getFreeMapId() {
		return new MapId(0);
	};
    
};
