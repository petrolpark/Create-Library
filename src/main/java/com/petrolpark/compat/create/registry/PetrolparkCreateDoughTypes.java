package com.petrolpark.compat.create.registry;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.world.dough.IDoughType;
import com.petrolpark.compat.create.core.world.dough.type.SimpleDoughType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkCreateDoughTypes {
    
    public static final RegistryEntry<IDoughType<?>, SimpleDoughType> TEST = Petrolpark.REGISTRATE.simple("test", PetrolparkCreateRegistries.Keys.DOUGH_TYPE, () -> new SimpleDoughType(Petrolpark.asResource("pasta_dough")));

    public static final void register() {};
};
