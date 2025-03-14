package com.petrolpark.compat;

import java.util.SortedSet;
import java.util.TreeSet;

public enum SharedFeatures {
    
    // Machines/Gameplay
    CENTRIFUGE,
    EXTRUSION,
    AGEING_BARREL,

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
    private boolean activated = false;

    SharedFeatures(SharedFeatures... dependencies) {
        this.dependencies = dependencies;
    };

    public boolean activated() {
        return activated;
    };

    public void require(Mods mod) {
        activated = true;
        users.add(mod);
        for (SharedFeatures feature : dependencies) feature.require(mod);
    };
};
