package petrolpark.mc.library.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.neoforged.fml.common.Mod;

/**
 * Marks a static method in a {@link Mod} class as a provider of {@link SharedFeatureFlag shared features} for the Petrolpark Library.
 * The method must return an array of {@link SharedFeatureFlag} and take no parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetPetrolparkSharedFeatures {
    
};
