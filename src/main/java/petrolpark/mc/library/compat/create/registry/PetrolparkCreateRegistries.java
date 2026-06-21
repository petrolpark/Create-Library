package petrolpark.mc.library.compat.create.registry;

import org.jetbrains.annotations.ApiStatus;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.dough.DoughCut;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.IDoughType;
import petrolpark.mc.library.compat.create.core.world.dough.topping.IDoughTopping;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.animation.MandrelAnimationType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class PetrolparkCreateRegistries {
    
    public static final Registry<MandrelAnimationType> MANDREL_ANIMATION_TYPES = PetrolparkRegistries.simple(Keys.MANDREL_ANIMATION_TYPE);

    public static final Registry<IDoughType<?>> DOUGH_TYPES = PetrolparkRegistries.simple(Keys.DOUGH_TYPE);
    public static final Registry<IDoughTopping.Type<?>> DOUGH_TOPPING_TYPES = PetrolparkRegistries.simple(Keys.DOUGH_TOPPING_TYPE);
    public static final Registry<IAdvancedIngredientType<? super DoughData>> DOUGH_INGREDIENT_TYPES = PetrolparkRegistries.simple(Keys.DOUGH_INGREDIENT_TYPE);

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
        public static final ResourceKey<Registry<IDoughTopping>> DOUGH_TOPPING = key("dough/topping"); // Data
        public static final ResourceKey<Registry<IDoughTopping.Type<?>>> DOUGH_TOPPING_TYPE = key("dough/topping_type");
        public static final ResourceKey<Registry<IAdvancedIngredientType<? super DoughData>>> DOUGH_INGREDIENT_TYPE = key("dough/ingredient_type");

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };
};
