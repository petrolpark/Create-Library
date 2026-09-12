package petrolpark.mc.library.core.world.entity.ai.behavior;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableLong;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

/**
 * Largely copied from {@link AcquirePoi}. A version whose search center is based on another memory, rather than around the entity itself.
 */
public class AcquirePoiNearBehavior {
    
    public static BehaviorControl<PathfinderMob> create(
        Predicate<Holder<PoiType>> acquirablePois,
        MemoryModuleType<GlobalPos> existingAbsentMemory,
        MemoryModuleType<GlobalPos> acquiringMemory,
        MemoryModuleType<GlobalPos> searchCenterMemory,
        int range,
        BiPredicate<ServerLevel, BlockPos> poiPredicate
    ) {
        final MutableLong retryTime = new MutableLong(0l);
        final Long2ObjectMap<AcquirePoi.JitteredLinearRetry> posRetries = new Long2ObjectOpenHashMap<>();
        final OneShot<PathfinderMob> oneShotBehavior = BehaviorBuilder.create(
            instance -> instance.group(
                instance.absent(acquiringMemory),
                instance.present(searchCenterMemory)
            ).apply(instance, (acquiringMemoryAccessor, searchCenterMemoryAccessor) ->
                (level, entity, time) -> {
                    if (retryTime.getValue() == 0l) {
                        retryTime.setValue(level.getGameTime() + (long)level.random.nextInt(20));
                        return false;
                    } else if (level.getGameTime() < retryTime.getValue()) {
                        return false;
                    } else {
                        final GlobalPos searchCenterGlobalPos = instance.get(searchCenterMemoryAccessor);
                        if (level.dimension() != searchCenterGlobalPos.dimension()) return false;

                        retryTime.setValue(time + 20l + (long)level.getRandom().nextInt(20));
                        final PoiManager poiManager = level.getPoiManager();
                        posRetries.long2ObjectEntrySet().removeIf(entry -> !entry.getValue().isStillValid(time));
                        final Predicate<BlockPos> posPredicate = (pos -> {
                            final AcquirePoi.JitteredLinearRetry retry = posRetries.get(pos.asLong());
                            if (retry == null) {
                                return poiPredicate.test(level, pos);
                            } else if (!retry.shouldRetry(time)) {
                                return false;
                            } else {
                                retry.markAttempt(time);
                                return poiPredicate.test(level, pos);
                            }
                        });
                        final Set<Pair<Holder<PoiType>, BlockPos>> set = poiManager.findAllClosestFirstWithType(
                                acquirablePois, posPredicate, searchCenterGlobalPos.pos(), range, PoiManager.Occupancy.HAS_SPACE
                            )
                            .limit(5l)
                            .collect(Collectors.toSet());
                        final Path path = AcquirePoi.findPathToPois(entity, set);
                        if (path != null && path.canReach()) {
                            final BlockPos targetPos = path.getTarget();
                            poiManager.getType(targetPos).ifPresent(p_340720_ -> {
                                poiManager.take(acquirablePois, (poiType, posToTake) -> posToTake.equals(targetPos), targetPos, 1);
                                acquiringMemoryAccessor.set(GlobalPos.of(level.dimension(), targetPos));
                                posRetries.clear();
                                DebugPackets.sendPoiTicketCountPacket(level, targetPos);
                            });
                        } else {
                            for (Pair<Holder<PoiType>, BlockPos> pair : set) {
                                posRetries.computeIfAbsent(
                                    pair.getSecond().asLong(), p_264881_ -> new AcquirePoi.JitteredLinearRetry(level.random, time)
                                );
                            }
                        }
                        return true;
                    }
                }
            )
        );
        return existingAbsentMemory == acquiringMemory
            ? oneShotBehavior
            : BehaviorBuilder.create(entity -> entity.group(entity.absent(existingAbsentMemory)).apply(entity, existingMemoryAccessor -> oneShotBehavior));
    };
};
