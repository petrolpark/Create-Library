package com.petrolpark.util;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayHelper {

    public static HitResult getHitResult(Entity entity, float partialTicks, boolean hitFluids) {
        return getHitResult(Collections.emptyList(), entity, partialTicks, hitFluids);
    };

    public static HitResult getHitResult(List<AABB> customBoxes, Entity entity, float partialTicks, boolean hitFluids) {
        double blockHitReach = getBlockReach(entity);
        double entityHitReach = getEntityReach(entity);
        double reach = Math.max(blockHitReach, entityHitReach); // Max out of entity and block reach (we need to make sure the targeted entity is not behind a block in the case that entityReach > blockReach)
        HitResult result = entity.pick(reach, partialTicks, hitFluids);
        Vec3 eyePos = entity.getEyePosition();
        double squareReach = reach * reach;
        double squareHitDistance = squareReach; // The square of the distance to whatever's been hit

        // Blocks
        if (result != null && result.getType() != HitResult.Type.MISS) {
            Vec3 blockHitLocation = result.getLocation();
            squareReach = blockHitLocation.distanceToSqr(eyePos); // Limit the reach so we don't look behind any blocks
            if (squareReach > blockHitReach * blockHitReach) { // If the block hit is farther than blockHitReach
                result = BlockHitResult.miss(blockHitLocation, Direction.getNearest(eyePos.x, eyePos.y, eyePos.z), BlockPos.containing(blockHitLocation));
            } else {
                squareHitDistance = squareReach;
            };
        };

        Vec3 view = entity.getViewVector(partialTicks);
        Vec3 ray = eyePos.add(view.scale(reach));

        // Entities
        AABB aabb = entity.getBoundingBox().expandTowards(view.scale(reach)).inflate(1d, 1d, 1d); // Possible zone for targetable Entities to be
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePos, ray, aabb, e -> !e.isSpectator() && e.isPickable(), squareReach);
        if (entityHitResult != null) {
            Vec3 entityHitLocation = entityHitResult.getLocation();
            double squareEntityDistance = eyePos.distanceToSqr(entityHitLocation);
            if (squareEntityDistance > squareReach || squareEntityDistance > entityHitReach * entityHitReach) { // If the entity is behind a block or unreachable
                result = BlockHitResult.miss(entityHitLocation, Direction.getNearest(view.x, view.y, view.z), BlockPos.containing(entityHitLocation));
            } else if (squareEntityDistance < squareReach || result == null) { // If the entity is within reach
                result = entityHitResult;
                squareHitDistance = squareEntityDistance;
            };
        };

        // Custom Boxes
        int i = 0;
        for (AABB box : customBoxes) {
            Optional<Vec3> hit = box.clip(eyePos, ray);
            if (hit.isPresent()) {
                Vec3 customBoxHitLocation = hit.get();
                double customBoxDistanceSquare = eyePos.distanceToSqr(customBoxHitLocation);
                if (customBoxDistanceSquare < squareHitDistance) {
                    result = new CustomHitResult(customBoxHitLocation, i);
                    squareHitDistance = customBoxDistanceSquare;
                };
            };
            i++;
        };

        return result;
    };

    public static double getBlockReach(Entity entity) {
        if (entity instanceof Player player) return player.blockInteractionRange();
        return 3d;
    };

    public static double getEntityReach(Entity entity) {
        if (entity instanceof Player player) return player.entityInteractionRange();
        return 3d;
    };

    public static class CustomHitResult extends HitResult {

        public final int index;

        protected CustomHitResult(Vec3 location, int index) {
            super(location);
            this.index = index;
        };

        @Override
        public HitResult.Type getType() {
            return HitResult.Type.ENTITY;
        };

    };

    /**
     * @param boxes
     * @param start
     * @param end
     * @return The index of the box which was hit, or {@code -1} if there was no hit
     */
    public static int getHit(List<AABB> boxes, Vec3 start, Vec3 end) {
        int hit = -1;
        int boxNo = 0;
        double minimumDistance = Double.MAX_VALUE;
        for (AABB box : boxes) {
            if (box.contains(start)) return boxNo;
            Optional<Vec3> hitVec = box.clip(start, end);
            if (hitVec.isPresent()) {
                double distance = start.distanceToSqr(hitVec.get());
                if (distance < minimumDistance) {
                    hit = boxNo;
                    minimumDistance = distance;
                };
            };
            boxNo++;
        };
        return hit;
    };
    
};
