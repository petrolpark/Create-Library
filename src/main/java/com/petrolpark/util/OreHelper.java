package com.petrolpark.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkAttributes;

import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class OreHelper {

    public static final List<PlacedFeature> PLACEABLE_ORE_FEATURES = new ArrayList<>();

    @SubscribeEvent
    public static final void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) return; // Server-only

        PLACEABLE_ORE_FEATURES.clear();
        PLACEABLE_ORE_FEATURES.addAll(event.getRegistryAccess().registryOrThrow(Registries.PLACED_FEATURE).stream()
            .map(placedFeature -> placedFeature.feature().value().config() instanceof OreConfiguration oreConfig 
                && placedFeature.feature().value().feature() instanceof OreFeature oreFeature
                && oreConfig.targetStates.stream().allMatch(targetState -> targetState.state.is(Tags.Blocks.ORES)) ? // Don't include diorite etc.
                    new PlacedFeature(
                        Holder.direct(new ConfiguredFeature<>(oreFeature, new OreConfiguration(oreConfig.targetStates, oreConfig.size, 1f))), // Always discard exposed Ores so the illusion that the ores were always there is maintained
                        placedFeature.placement() 
                    ) 
                : null
            ).filter(obj -> obj != null)
            .toList()
        );

        Petrolpark.LOGGER.info("Found {} Ore Features", PLACEABLE_ORE_FEATURES.size());
    };

    
    @SubscribeEvent
    public static final void onBreakBlock(BlockEvent.BreakEvent event) {
        if (
            event.getLevel() instanceof ServerLevel level 
            && level.getRandom().nextFloat() <= event.getPlayer().getAttributeValue(PetrolparkAttributes.ORE_DISCOVERY_CHANCE.getDelegate())
            && !event.getState().is(Tags.Blocks.ORES)
        ) placeRandomOre(level, event.getPlayer().getEyePosition(), event.getPlayer().getViewVector(1f));
    };

    /**
     * Place a random ore blob hidden behind a block from the player, weighted for the appropriate y level
     * @param level
     * @param origin Where to originate the attempted placement
     * @param direction The direction the ore should be from this origin
     */
    public static final void placeRandomOre(ServerLevel level, Vec3 origin, Vec3 direction) {
        for (ConfiguredFeature<?, ?> configuredFeature : PLACEABLE_ORE_FEATURES.stream()
            .map(placedFeature -> IntAttached.of(placedFeature, getVerticalDistanceToClosestPlacement(level, getCenterPos(level, origin, direction, placedFeature.feature().value()), placedFeature)))
            .filter(pair -> pair.getSecond().isPresent()) // Impossible to place e.g. due to biome restrictions
            .sorted((pair1, pair2) -> pair1.getSecond().getAsInt() - pair2.getSecond().getAsInt())
            .map(pair -> pair.getFirst())
            .map(PlacedFeature::feature)
            .map(Holder::value)
            .toList()
        ) {
            final BlockPos pos = getCenterPos(level, origin, direction, configuredFeature);
            if (configuredFeature.place(level, level.getChunkSource().getGenerator(), level.getRandom(), pos)) {
                // Sync surrounding chunks
                final ChunkPos chunkPos = new ChunkPos(pos);
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        level.getChunkSource().chunkMap.onChunkReadyToSend(level.getChunk(chunkPos.x + x, chunkPos.z + z));
                    };
                }
                return;
            };
        };
    };

    /**
     * For an Ore feature, generate every attempted placement position and find the closest one to the given BlockPos vertically.
     * More common ores for the given y level (e.g. coal near the surface) will on expectation be closer, so will appear more often.
     * @param level
     * @param targetPos
     * @param placedFeature
     */
    public static final OptionalInt getVerticalDistanceToClosestPlacement(ServerLevel level, BlockPos targetPos, PlacedFeature placedFeature) {
        final PlacementContext placementContext = new PlacementContext(level, level.getChunkSource().getGenerator(), Optional.of(placedFeature));
        Stream<BlockPos> stream = Stream.of(targetPos);
        for (PlacementModifier placementModifier : placedFeature.placement()) {
            if (placementModifier instanceof InSquarePlacement) continue; // Ignore as we don't care about horizontal offset
            stream = stream.flatMap(pos -> placementModifier.getPositions(placementContext, level.getRandom(), pos));
            List<BlockPos> ps = stream.toList();
            stream = ps.stream();
        };
        return stream.mapToInt(pos -> Math.abs(targetPos.getY() - pos.getY())).min();
    };

    public static final BlockPos getCenterPos(Level level, Vec3 origin, Vec3 direction, ConfiguredFeature<?, ?> oreFeature) {
        final float size = oreFeature.config() instanceof OreConfiguration oreConfig ? oreConfig.size : 1f;
        final float radius = 0.2952f * (float)Math.pow(size, 2 / 3d);
        return BlockPos.containing(origin.add(direction.scale(2f + radius)).add(0f, radius, 0f));
    };
};
