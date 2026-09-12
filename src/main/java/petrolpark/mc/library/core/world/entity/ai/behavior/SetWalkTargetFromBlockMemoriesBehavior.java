package petrolpark.mc.library.core.world.entity.ai.behavior;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.util.AiHelper;

/**
 * Largely copied from {@link SetWalkTargetFromBlockMemory}, but with a fallback location if the first one cannot be reached.
 */
public class SetWalkTargetFromBlockMemoriesBehavior {

    public static OneShot<PathfinderMob> create(MemoryModuleType<GlobalPos> targetMemory, MemoryModuleType<GlobalPos> fallbackTargetMemory, float speedModifier, int closeEnoughDist, int tooFarDistance, int tooLongUnreachableDuration) {
        return BehaviorBuilder.create(
            instance -> instance.group(
                instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE),
                instance.absent(MemoryModuleType.WALK_TARGET),
                instance.present(targetMemory),
                instance.registered(fallbackTargetMemory)
            ).apply(instance, (cantReachSinceAccessor, walkTargetAccessor, targetAccessor, fallbackTargetAccessor) -> (level, entity, gameTime) -> {
                final Optional<Long> cantReachSinceTime = instance.tryGet(cantReachSinceAccessor);
                final boolean timedOut = cantReachSinceTime.isPresent() && level.getGameTime() - cantReachSinceTime.get() > (long)tooLongUnreachableDuration;
                
                if (!timedOut && setTargetTowards(level, entity, walkTargetAccessor, instance.get(targetAccessor), speedModifier, tooFarDistance, closeEnoughDist))
                    return true;

                AiHelper.releasePoi(level, entity, targetMemory);
                targetAccessor.erase();

                final GlobalPos fallbackPos = instance.tryGet(fallbackTargetAccessor).orElse(null);
                if (fallbackPos != null && !timedOut && setTargetTowards(level, entity, walkTargetAccessor, fallbackPos, speedModifier, tooFarDistance, closeEnoughDist))
                    return true;

                AiHelper.releasePoi(level, entity, fallbackTargetMemory);
                fallbackTargetAccessor.erase();
                cantReachSinceAccessor.set(gameTime);

                return true;
            })
        );
    };

    public static boolean setTargetTowards(ServerLevel level, PathfinderMob entity, MemoryAccessor<?, WalkTarget> walkTargetAccessor, GlobalPos targetPos, float speedModifier, int tooFarDistance, int closeEnoughDist) {
        if (targetPos.dimension() != level.dimension()) return false;
        if (targetPos.pos().distManhattan(entity.blockPosition()) > tooFarDistance) {
            Vec3 randomPos = null;
            int i = 0;

            while (randomPos == null || BlockPos.containing(randomPos).distManhattan(entity.blockPosition()) > tooFarDistance) {
                randomPos = DefaultRandomPos.getPosTowards(entity, 15, 7, Vec3.atBottomCenterOf(targetPos.pos()), Math.PI / 2f);
                if (++i == 1000) {
                    return false;
                };
            };
            
            walkTargetAccessor.set(new WalkTarget(randomPos, speedModifier, closeEnoughDist));
            return true; 
        } else if (targetPos.pos().distManhattan(entity.blockPosition()) > closeEnoughDist) {
            walkTargetAccessor.set(new WalkTarget(targetPos.pos(), speedModifier, closeEnoughDist));
            return true;
        } else {
            return false;
        }
    };
};

