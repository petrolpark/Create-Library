package com.petrolpark.compat.create;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.compat.create.common.processing.mandrel.animation.MandrelAnimationType;
import com.petrolpark.compat.create.core.dough.DoughCut;
import com.petrolpark.compat.create.core.dough.IDoughType;
import com.petrolpark.compat.create.core.dough.topping.IDoughTopping;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class PetrolparkCreateRegistries {
    
    public static final Registry<MandrelAnimationType> MANDREL_ANIMATION_TYPES = PetrolparkRegistries.simple(Keys.MANDREL_ANIMATION_TYPE);

    public static final Registry<IDoughType<?>> DOUGH_TYPES = PetrolparkRegistries.simple(Keys.DOUGH_TYPE);
    public static final Registry<IDoughTopping.Type<?>> DOUGH_TOPPING_TYPES = PetrolparkRegistries.simple(Keys.DOUGH_TOPPING_TYPE);

    @ApiStatus.Internal
	public static void init() {
		// make sure the class is loaded.
		// this method is called at the tail of BuiltInRegistries, injected by CreateBuiltInRegistriesMixin.
	};
    
    public static class Keys {

        public static final ResourceKey<Registry<MandrelAnimationType>> MANDREL_ANIMATION_TYPE = key("mandrel_animation_type");

        // Dough
        public static final ResourceKey<Registry<IDoughType<?>>> DOUGH_TYPE = key("dough/type");
        public static final ResourceKey<Registry<DoughCut>> DOUGH_CUT = key("dough/cut"); // Data
        public static final ResourceKey<Registry<IDoughTopping>> DOUGH_TOPPING = key("dough/topping");
        public static final ResourceKey<Registry<IDoughTopping.Type<?>>> DOUGH_TOPPING_TYPE = key("dough/topping_type");

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };
};
