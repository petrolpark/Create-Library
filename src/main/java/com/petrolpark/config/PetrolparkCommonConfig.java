package com.petrolpark.config;

import net.createmod.catnip.config.ConfigBase;

public class PetrolparkCommonConfig extends ConfigBase {

    public final ConfigBool glassBottleFluidCapability = b(true, "glassBottleFluidCapability", "Add NeoForge Fluid Capability to Glass Bottle items, making them act more like Buckets", "[May override other mods' functionality]");

    // Compat
    public final ConfigGroup compatibility = group(0, "compatibility");
        // Create
        public final ConfigGroup create = group(1, "create");
        public final ConfigFloat createHorseMillStressCapacityAttributeMax = f(8192f, 256f, Float.MAX_VALUE, "horseMillStressCapacityAttributeMaximum", "The maximum Stress Capacity a Horse can provide on a Horse Mill");
        // Brewin n Chewin
        public final ConfigGroup brewinAndChewin = group(1, "brewinAndChewin");
        public final ConfigBool brewinAndChewinFermentingInLiddedBasin = b(true, "fermentingInLiddedBasin", "Some Fermenting/Pouring Recipes are possible in Lidded Basins and Spouts, if enabled.");

    @Override
    public String getName() {
        return "Common";
    };
    
};
