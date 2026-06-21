package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.world.levelgen.feature.tree.ThickBaseRootPlacer;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;

public class PetrolparkFeatureTypes {
    
    public static final RegistryEntry<RootPlacerType<?>, RootPlacerType<ThickBaseRootPlacer>> THICK_BASE_ROOT_PLACER = REGISTRATE.simple("thick_base_root_placer", Registries.ROOT_PLACER_TYPE, () -> new RootPlacerType<>(ThickBaseRootPlacer.CODEC));

    public static final void register() {};
};
