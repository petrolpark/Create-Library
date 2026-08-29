package petrolpark.mc.library.shared.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.Petrolpark;

public class SharedDamageSources {

    public static final ResourceKey<DamageType>

    ALCOHOL_POISONING = ResourceKey.create(Registries.DAMAGE_TYPE, Petrolpark.asResource("shared/alcohol_poisoning")),
    HEADACHE = ResourceKey.create(Registries.DAMAGE_TYPE, Petrolpark.asResource("shared/headache"));
  
    public static final DamageSource alcoholPoisoning(Level level) {
        return new DamageSource(level.registryAccess().holderOrThrow(ALCOHOL_POISONING));
    };

    public static final DamageSource headache(Level level) {
        return new DamageSource(level.registryAccess().holderOrThrow(HEADACHE));
    };
};
