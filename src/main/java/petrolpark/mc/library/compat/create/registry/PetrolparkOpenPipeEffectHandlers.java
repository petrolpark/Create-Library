package petrolpark.mc.library.compat.create.registry;

import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.create.core.world.fluid.openPipeEffect.MobEffectOpenPipeEffectHandler;
import petrolpark.mc.library.shared.registry.SharedMobEffects;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.registry.SimpleRegistry;

public class PetrolparkOpenPipeEffectHandlers {
    
    public static final void register() {

        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(PetrolparkTags.Fluids.COOKING_OILS, new MobEffectOpenPipeEffectHandler(SharedMobEffects.SLIPPING.asInstanceSupplier(21, 0))));
    };
};
