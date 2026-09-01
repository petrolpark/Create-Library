package petrolpark.mc.library.compat.create.registry;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.animation.MandrelAnimationType;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public class PetrolparkCreateRegistries {
    
    public static final Registry<MandrelAnimationType> MANDREL_ANIMATION_TYPES = PetrolparkRegistries.simple(Keys.MANDREL_ANIMATION_TYPE);

    @ApiStatus.Internal
	public static void init() {
		// make sure the class is loaded.
		// this method is called at the tail of BuiltInRegistries, injected by CreateBuiltInRegistriesMixin.
	};
    
    public static class Keys {

        public static final ResourceKey<Registry<MandrelAnimationType>> MANDREL_ANIMATION_TYPE = key("mandrel_animation_type");

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };
};
