package com.petrolpark.config;

import net.createmod.catnip.config.ConfigBase;

public class PetrolparkCommonConfig extends ConfigBase {

    // Compat
    public final ConfigGroup compatibility = group(0, "compatibility");

    // Brewin n Chewin
    public final ConfigGroup brewinAndChewin = group(1, "brewinAndChewin");
    public final ConfigBool brewinAndChewinFermentingInLiddedBasin = b(true, "fermentingInLiddedBasin", "Some Fermenting/Pouring Recipes are possible in Lidded Basins and Spouts, if enabled.");

    @Override
    public String getName() {
        return "Common";
    };
    
};
