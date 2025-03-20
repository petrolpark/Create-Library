package com.petrolpark.util;

import java.util.Comparator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MathsHelper {

    public static final int floorLog(int x, int base) {
        int result = 0;
        while (x >= base) {
            x /= base;
            result++;
        };
        return result;
    };

    public static int exponentiate(int base, int exponent) {
        int result = 1;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result *= base;
            base *= base;
            exponent >>= 1;
        };
        return result;
    }

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

    public static double azimuth(Vec3 vec) {
        return Math.atan2(vec.x, vec.z);
    };

    public static double inclination(Vec3 vec) {
        return Math.atan2(Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z()), vec.y());
    };

    public static AABB expandToInclude(AABB box, Vec3 point) {
        return new AABB(Math.min(box.minX, point.x), Math.min(box.minY, point.y), Math.min(box.minZ, point.z), Math.max(box.maxX, point.x), Math.max(box.maxY, point.y), Math.max(box.maxZ, point.z));
    };

    public static AABB expandToInclude(AABB box, BlockPos pos) {
        return expandToInclude(expandToInclude(box, Vec3.atLowerCornerOf(pos)), Vec3.atLowerCornerOf(pos).add(1d, 1d, 1d));
    };

    public static double volume(AABB box) {
        return (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ);
    };
};
