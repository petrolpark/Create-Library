package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

public class PetrolparkParticleTypes {
  
    public static final RegistryEntry<ParticleType<?>, SimpleParticleType> AIR_BUBBLE = REGISTRATE.particleType("air_bubble", () -> new SimpleParticleType(false));
    public static final RegistryEntry<ParticleType<?>, SimpleParticleType> GOLD = REGISTRATE.particleType("gold", () -> new SimpleParticleType(false));

    public static final void register() {};
};
