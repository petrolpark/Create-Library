package com.petrolpark.mobeffects;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.ForgeRegistries;

public class PetrolparkMobEffects {
    
    public static final RegistryEntry<SimpleMobEffect> NUMBNESS = REGISTRATE.get().simple("numbness", ForgeRegistries.Keys.MOB_EFFECTS, () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0x7A2337));

    public static final void register() {};
};
