package com.petrolpark.compat;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.petrolpark.util.Lang;

import net.minecraft.util.StringRepresentable;

public enum SharedFeatureFlag implements StringRepresentable {
    
    // Machines/Gameplay
    CENTRIFUGE,
    EXTRUSION,
    BASIN_LID,
    TORQUE_LIMITER,

    // Items/Fluids
    MESH,
    EGG_PRODUCTS, // Egg Whites & Yolks, Meringue
    SUNFLOWER_OIL,
    MILK_PRODUCTS(CENTRIFUGE),
    SPRING,

    MANDREL(SPRING),
    ;

    public static final Codec<SharedFeatureFlag> CODEC = StringRepresentable.fromEnum(SharedFeatureFlag::values);

    private final SharedFeatureFlag[] dependencies;
    
    private final SortedSet<Mods> users = new TreeSet<>(Mods::compareTo);
    private boolean enabled = false;

    SharedFeatureFlag(SharedFeatureFlag... dependencies) {
        this.dependencies = dependencies;
    };

    public boolean enabled() {
        return enabled;
    };

    public void enable(Mods mod) {
        enabled = true;
        users.add(mod);
        for (SharedFeatureFlag feature : dependencies) feature.enable(mod);
    };

    @ApiStatus.Internal
    public static void enableAll() {
        for (SharedFeatureFlag feature : values()) feature.enable(Mods.PETROLPARK);
    };

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    };

    public Stream<Mods> streamUsers() {
        return users.stream();
    };
};
