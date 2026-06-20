package com.petrolpark.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.core.world.ChunkTickEvent;
import com.petrolpark.core.world.block.HandlePrecipitationEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.WritableLevelData;
import net.neoforged.neoforge.common.NeoForge;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    
    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess,
            Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide,
            boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed,
                maxChainedNeighborUpdates);
        throw new AssertionError();
    };

    @Inject(
        method = "tickChunk",
        at = @At("HEAD")
    )
    public void petrolpark$postChunkTickEventPre(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ChunkTickEvent.Pre(chunk, randomTickSpeed));
    };

    @Inject(
        method = "tickChunk",
        at = @At("TAIL")
    )
    public void petrolpark$postChunkTickEventPost(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ChunkTickEvent.Post(chunk, randomTickSpeed));
    };

    @WrapOperation(
        method = "tickPrecipitation",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"
        )
    )
    public void petrolpark$postPrecipitationEvent(Block block, BlockState state, Level level, BlockPos motionBlockingTopPos, Biome.Precipitation precipitation, Operation<Void> original, BlockPos randomPos) {
        // Top motion-blocking block, so excludes plants etc.
        final HandlePrecipitationEvent event = new HandlePrecipitationEvent(level, motionBlockingTopPos, state, precipitation);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) original.call(block, state, level, motionBlockingTopPos, precipitation);

        // Top block, including non-motion blocking
        final BlockPos nonMotionBlockingTopPos = getHeightmapPos(Heightmap.Types.WORLD_SURFACE, randomPos).below();
        if (!nonMotionBlockingTopPos.equals(motionBlockingTopPos)) NeoForge.EVENT_BUS.post(new HandlePrecipitationEvent(level, nonMotionBlockingTopPos, getBlockState(nonMotionBlockingTopPos), precipitation));
    };

    
};
