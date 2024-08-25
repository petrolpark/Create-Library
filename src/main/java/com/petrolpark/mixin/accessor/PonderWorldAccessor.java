package com.petrolpark.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.foundation.ponder.PonderWorld;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;

@Mixin(PonderWorld.class)
public interface PonderWorldAccessor {
    
    @Invoker(
        value = "makeParticle",
        remap = false
    )
    public <T extends ParticleOptions> Particle invokeMakeParticle(T data, double x, double y, double z, double mx, double my, double mz);
};
