package petrolpark.mc.library.compat.create.shared.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.Petrolpark;

public class SharedCreateDamageSources {

    public static final ResourceKey<DamageType>

    BLENDER = ResourceKey.create(Registries.DAMAGE_TYPE, Petrolpark.asResource("shared/blender")),
    EXTRUSION_DIE = ResourceKey.create(Registries.DAMAGE_TYPE, Petrolpark.asResource("shared/extrusion_die"));
  
    public static final DamageSource blender(Level level) {
        return new DamageSource(level.registryAccess().holderOrThrow(BLENDER));
    };

    public static final DamageSource extrusionDie(Level level) {
        return new DamageSource(level.registryAccess().holderOrThrow(EXTRUSION_DIE));
    };
};
