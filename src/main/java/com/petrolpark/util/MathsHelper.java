package com.petrolpark.util;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MathsHelper {
    /**
     * The directional angle in degrees between two vectors, between 0 and 360.
     * @param vec1
     * @param vec2
     * @param plane The approximate vector around which {@code vec1} was rotated to get {@code vec2}
     */
    public static double angleBetween(Vec3 vec1, Vec3 vec2, Vec3 plane) {
        double angle = Math.acos(vec1.dot(vec2) / (vec1.length() * vec2.length())) * 180d / Mth.PI;
        if (vec1.dot(vec2.cross(plane)) < 0d) angle = 360d - angle;
        return angle;
    };

    /**
     * @param vec The vector to rotate
     * @param rotationAxis The vector about which to rotate the first vector
     * @param angle The angle in degrees through which to rotate the first vector around the second
     */
    public static Vec3 rotate(Vec3 vec, Vec3 rotationAxis, double angle) {
        Vec3 k = rotationAxis.normalize();
        double angleInRads = angle * Mth.PI / 180d;
        // Rodrigues' formula
        return vec.scale(Math.cos(angleInRads))
            .add(k.cross(vec).scale(Math.sin(angleInRads)))
            .add(k.scale(k.dot(vec) * (1 - Math.cos(angleInRads)))); 
    };

    public static Vec3i add(Vec3i vec1, Vec3i vec2) {
        return new Vec3i(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    };

    public static Comparator<Vec3> getClosest(Vec3 targetVector) {
        return (v1, v2) -> Double.compare(v1.dot(targetVector), v2.dot(targetVector));
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
