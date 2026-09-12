package petrolpark.mc.library.shared;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforgespi.language.ModFileScanData;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.registry.SharedAttachmentTypes;
import petrolpark.mc.library.shared.registry.SharedBlockEntityTypes;
import petrolpark.mc.library.shared.registry.SharedBlocks;
import petrolpark.mc.library.shared.registry.SharedItems;
import petrolpark.mc.library.shared.registry.SharedMemoryModuleTypes;
import petrolpark.mc.library.shared.registry.SharedMobEffects;
import petrolpark.mc.library.shared.registry.SharedParticleTypes;
import petrolpark.mc.library.shared.registry.SharedRecipeSerializers;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;

/**
 * Register any objects {@link SharedFeatureFlag shared} between dependent mods.
 */
public class Shared {
    
    public static final void ctor(IEventBus modEventBus, ModContainer modContainer) {

        initializeSharedFeatures();

        // Registration
        SharedAttachmentTypes.register(modEventBus);
        SharedBlockEntityTypes.register();
        SharedBlocks.register();
        SharedItems.register();
        SharedMemoryModuleTypes.register();
        SharedMobEffects.register();
        SharedParticleTypes.register();
        SharedRecipeSerializers.register();
        SharedRecipeTypes.register();
    };

    private static final void initializeSharedFeatures() {
        Petrolpark.LOGGER.info("Searching for Mods enabling Petrolpark's Shared Features");
        for (final ModFileScanData scanData : ModList.get().getAllScanData()) {

            scanData.getAnnotatedBy(GetPetrolparkSharedFeatures.class, ElementType.METHOD).forEach(data -> {
                final String className = data.clazz().getClassName();
                final String memberName = data.memberName().split("\\(")[0];
                Petrolpark.LOGGER.info("Found suitable method " + memberName + "in class " + className);
                try {
                    final Class<?> clazz = Class.forName(className);
                    final Mod mod = clazz.getAnnotation(Mod.class);
                    if (mod == null) throw new IllegalArgumentException("@GetPetrolparkSharedFeatures method must be in @Mod class");
                    Mods compatMod = Mods.LOOKUP.apply(mod.value());
                    if (compatMod == null) compatMod = Mods.PETROLPARK; // Other Mods can enable Shared Features under the Petrolpark name
                    final Method method = clazz.getMethod(memberName);
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (method.invoke(null) instanceof SharedFeatureFlag[] flags) {
                            for (SharedFeatureFlag flag : flags) flag.enable(compatMod);
                        } else {
                            throw new IllegalArgumentException("Must return an array of SharedFeatureFlag");
                        };
                    } else {
                        throw new IllegalArgumentException("@GetPetrolparkSharedFeatures method must be static");
                    };
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException("Could not initialize Shared Features in class " + className, e);
                };
            });
        };
    };

};
