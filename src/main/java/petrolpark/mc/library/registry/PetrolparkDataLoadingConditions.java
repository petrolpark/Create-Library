package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.data.condition.ConfigBooleanCondition;
import petrolpark.mc.library.core.data.condition.FluidTagEmptyCondition;
import petrolpark.mc.library.core.data.condition.SharedFeatureEnabledCondition;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.neoforged.neoforge.common.conditions.ICondition;

public class PetrolparkDataLoadingConditions {
    
    public static final RegistryEntry<MapCodec<? extends ICondition>, MapCodec<ConfigBooleanCondition>> CONFIG_BOOLEAN = REGISTRATE.dataLoadingCondition("config_bool", ConfigBooleanCondition.CODEC);
    public static final RegistryEntry<MapCodec<? extends ICondition>, MapCodec<SharedFeatureEnabledCondition>> FEATURE_ENABLED = REGISTRATE.dataLoadingCondition("feature_enabled", SharedFeatureEnabledCondition.CODEC);
    public static final RegistryEntry<MapCodec<? extends ICondition>, MapCodec<FluidTagEmptyCondition>> FLUID_TAG_EMPTY = REGISTRATE.dataLoadingCondition("fluid_tag_empty", FluidTagEmptyCondition.CODEC);

    public static final void register() {};
};
