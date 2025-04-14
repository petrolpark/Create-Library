package com.petrolpark.core.simulation;

import java.io.IOException;
import java.net.Proxy;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ServicesKeySet;

import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

@ApiStatus.Experimental
public class SimulatedServer extends MinecraftServer {

    /**
     * @see MinecraftServer#isDedicatedServer()
     */
    protected final boolean dedicated;

    protected final SampleLogger sampleLogger = new LocalSampleLogger(4);
    protected final AlwaysSprintingTickRateManager tickRateManager; // Overrides private field of same name

    @SuppressWarnings("null")
    public SimulatedServer(
        Thread serverThread,
        LevelStorageAccess storageAccess, 
        PackRepository packRepository,
        WorldStem worldStem,
        boolean dedicated
    ) {
        super(
            serverThread,
            storageAccess,
            packRepository,
            worldStem,
            Proxy.NO_PROXY,
            DataFixers.getDataFixer(),
            new Services(null, ServicesKeySet.EMPTY, null, null),
            LoggerChunkProgressListener::createFromGameruleRadius
        );

        this.dedicated = dedicated;

        tickRateManager = new AlwaysSprintingTickRateManager(this);
    };

    @Override
    protected boolean initServer() throws IOException {
        setPlayerList(new PlayerList(this, registries(), playerDataStorage, 0) {});
        loadLevel();
        return true;
    };

    @Override
    @SuppressWarnings("deprecation")
    protected void createLevels(@Nonnull ChunkProgressListener listener) {
        SimulatedServerLevel overworld = new SimulatedServerLevel(
            this,
            Util.backgroundExecutor(), // This is what this.executor is set to anyway
            storageSource,
            worldData.overworldData(),
            Level.OVERWORLD,
            registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM).get(LevelStem.OVERWORLD),
            listener,
            BiomeManager.obfuscateSeed(worldData.worldGenOptions().seed())
        );
        forgeGetWorldMap().put(Level.OVERWORLD, overworld); // Fine to access (ignore deprecation warning) as we are initializing this server
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(overworld));
        worldData.overworldData().setInitialized(true);
        getPlayerList().addWorldborderListener(overworld);
        // Don't create any other dimensions
    };

    @Override
    protected void runServer() {
        // try {
        //     nextTickTimeNanos = Util.getNanos();
        //     while (isRunning()) {
        //         long nanosToNextTick;
        //         if (!isPaused() && tickRateManager.isSprinting() && tickRateManager.checkShouldSprintThisTick()) {
        //             nanosToNextTick = 0l;
        //             nextTickTimeNanos = Util.getNanos();
        //         } else {
        //             nanosToNextTick = tickRateManager.nanosecondsPerTick();
        //         };

        //         boolean sprinting = nanosToNextTick == 0L;

        //         nextTickTimeNanos += nanosToNextTick;
        //         getProfiler().push("tick");
        //         tickServer(() -> true);
        //         getProfiler().popPush("nextTickWait");
        //         waitUntilNextTick();

        //         if (sprinting) tickRateManager.endTickWork();
                
        //         getProfiler().pop();
        //         this.isReady = true;
        //     };
        // } catch (Throwable throwable) {
        //     LOGGER.error("Encountered an unexpected exception", throwable);
        //     CrashReport crashreport = constructOrExtractCrashReport(throwable);
        //     fillSystemReport(crashreport.getSystemReport());
        //     Path path = getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
        //     if (crashreport.saveToFile(path, ReportType.CRASH)) {
        //         LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
        //     } else {
        //         LOGGER.error("We were unable to save this crash report to disk.");
        //     };
            
        //     onServerCrash(crashreport);
        // } finally {
        //     try {
        //         this.stopped = true;
        //         stopServer();
        //     } catch (Throwable throwable) {
        //         LOGGER.error("Exception stopping the server", throwable);
        //     } finally {
        //         if (services.profileCache() != null) services.profileCache().clearExecutor();
        //         onServerExit();
        //     };
        // };
    };

    @Override
    public void onServerExit() {
        // TODO Auto-generated method stub
        super.onServerExit();
    };

    @Override
    public void onServerCrash(@Nonnull CrashReport report) {
        // TODO Auto-generated method stub
        super.onServerCrash(report);
    };

    @Override
    public ServerTickRateManager tickRateManager() {
        return tickRateManager;
    };

    @Override
    public boolean isHardcore() {
        return false;
    };

    @Override
    public int getOperatorUserPermissionLevel() {
        return 0;
    };

    @Override
    public int getFunctionCompilationLevel() {
        return 4;
    };

    @Override
    public boolean shouldRconBroadcast() {
        return false; // No network
    };

    @Override
    protected SampleLogger getTickTimeLogger() {
        return sampleLogger;
    };

    @Override
    public boolean isTickTimeLoggingEnabled() {
        return false;
    };

    @Override
    public boolean isTimeProfilerRunning() {
        return false;
    };

    @Override
    public void startTimeProfiler() {};

    @Override
    public ProfileResults stopTimeProfiler() {
        return EmptyProfileResults.EMPTY;
    };

    @Override
    public SystemReport fillServerSystemReport(@Nonnull SystemReport report) {
        report.setDetail("Type", () -> String.format("Simulated {} server", isDedicatedServer() ? "dedicated" : "integrated"));
        return report;
    };

    @Override
    public boolean isDedicatedServer() {
        return dedicated;
    };

    @Override
    public int getRateLimitPacketsPerSecond() {
        return 0; // Should be no packets anyway
    };

    @Override
    public boolean isEpollEnabled() {
        return false;
    };

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    };

    @Override
    public boolean isPublished() {
        return false; // Never multiplayer
    };

    @Override
    public boolean shouldInformAdmins() {
        return false; // No operator players in the world so they don't need to see logs
    };

    @Override
    public boolean isSingleplayerOwner(@Nonnull GameProfile profile) {
        return false;
    };
    
};
