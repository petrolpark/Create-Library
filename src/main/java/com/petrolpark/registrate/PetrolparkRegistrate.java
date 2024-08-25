package com.petrolpark.registrate;

import com.petrolpark.badge.Badge;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class PetrolparkRegistrate extends CreateRegistrate {

    public PetrolparkRegistrate(String modid) {
        super(modid);
    };

    public BadgeBuilder<Badge, PetrolparkRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeBuilder<T, PetrolparkRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeBuilder<T, PetrolparkRegistrate>) entry(name, c -> BadgeBuilder.create(this, this, name, c, factory));
	};
    
};
