package com.petrolpark.mixin.compat.create.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.createmod.ponder.api.level.PonderLevel;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;

@Mixin(PonderLevel.class)
public interface PonderLevelAccessor {
    
    @Invoker(
        value = "makeParticle",
        remap = false
    )
    public <T extends ParticleOptions> Particle invokeMakeParticle(T data, double x, double y, double z, double mx, double my, double mz);
};
