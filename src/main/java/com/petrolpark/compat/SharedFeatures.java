package com.petrolpark.compat;

import java.util.SortedSet;
import java.util.TreeSet;

public enum SharedFeatures {
    
    // Machines/Gameplay
    CENTRIFUGE,
    EXTRUSION,
    AGEING_BARREL,
    TORQUE_LIMITER,

    // Items/Fluids
    MESH,
    EGG_PRODUCTS, // Egg Whites & Yolks, Meringue
    SUNFLOWER_OIL,
    MILK_PRODUCTS(CENTRIFUGE),
    SPRING,

    MANDREL(SPRING),
    ;


    private final SharedFeatures[] dependencies;
    
    private final SortedSet<Mods> users = new TreeSet<>(Mods::compareTo);
    private boolean enabled = false;

    SharedFeatures(SharedFeatures... dependencies) {
        this.dependencies = dependencies;
    };

    public boolean enabled() {
        return enabled;
    };

    public void enable(Mods mod) {
        enabled = true;
        users.add(mod);
        for (SharedFeatures feature : dependencies) feature.enable(mod);
    };
};
