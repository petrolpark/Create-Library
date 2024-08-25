package com.petrolpark.client.ponder.particle;

import java.util.Random;

import com.petrolpark.mixin.accessor.PonderWorldAccessor;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction.Emitter;

import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PetrolparkEmitters {

    private static final Random RANDOM = new Random();

    private static final <T extends ParticleOptions> Emitter inAABB(T data, AABB aabb, double vx, double vy, double vz) {
        return (w, x, y, z) -> w.addParticle(
            data,
            x + aabb.minX + RANDOM.nextFloat() * aabb.getXsize(),
            z + aabb.minY + RANDOM.nextFloat() * aabb.getYsize(),
            z + aabb.minZ + RANDOM.nextFloat() * aabb.getZsize(),
            vx, vy, vz
        );
    };

    public static final <T extends ParticleOptions> Emitter inAABB(T data, AABB aabb, Vec3 velocity) {
        return inAABB(data, aabb, velocity.x, velocity.y, velocity.z);
    };

    public static final Emitter fireworkBall(double speed, int size, int[] colors, int[] fadeColors, boolean trail, boolean twinkle) {
        return (w, x, y, z) -> {
            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double vx = (double)j + (RANDOM.nextDouble() - RANDOM.nextDouble()) * 0.5d;
                        double vy = (double)i + (RANDOM.nextDouble() - RANDOM.nextDouble()) * 0.5d;
                        double vz = (double)k + (RANDOM.nextDouble() - RANDOM.nextDouble()) * 0.5d;
                        double v = Math.sqrt(vx * vx + vy * vy + vz * vz) / speed + RANDOM.nextGaussian() * 0.05d;
                        createFireworkSpark(w, x, y, z, vx / v, vy / v, vz / v, colors, fadeColors, trail, twinkle);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2 - 1;
                        };
                    };
                };
            };
        };
    };

    public static void createFireworkSpark(PonderWorld world, double x, double y, double z, double vx, double vy, double vz, int[] colors, int[] fadeColors, boolean trail, boolean twinkle) {
        FireworkParticles.SparkParticle particle = (FireworkParticles.SparkParticle)((PonderWorldAccessor)world).invokeMakeParticle(ParticleTypes.FIREWORK, x, y, z, vx, vy, vz);
        particle.setTrail(trail);
        particle.setFlicker(twinkle);
        particle.setColor(colors[RANDOM.nextInt(colors.length)]);
        if (fadeColors.length > 0) particle.setFadeColor(fadeColors[RANDOM.nextInt(fadeColors.length)]);
        world.addParticle(particle);
    };

};
