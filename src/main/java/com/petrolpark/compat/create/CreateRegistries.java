package com.petrolpark.compat.create;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.compat.create.common.processing.mandrel.animation.MandrelAnimationType;
import com.petrolpark.compat.create.core.dough.DoughCut;
import com.petrolpark.compat.create.core.dough.DoughType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class CreateRegistries {
    
    public static final Registry<MandrelAnimationType> MANDREL_ANIMATION_TYPES = PetrolparkRegistries.simple(Keys.MANDREL_ANIMATION_TYPE);

    @ApiStatus.Internal
	public static void init() {
		// make sure the class is loaded.
		// this method is called at the tail of BuiltInRegistries, injected by CreateBuiltInRegistriesMixin.
	};
    
    public static class Keys {

        public static final ResourceKey<Registry<MandrelAnimationType>> MANDREL_ANIMATION_TYPE = key("mandrel_animation_type");

        // Dough
        public static final ResourceKey<Registry<DoughType>> DOUGH_TYPE = key("dough_type"); // Data
        public static final ResourceKey<Registry<DoughCut>> DOUGH_CUT = key("dough_cut"); // Data

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };
};
