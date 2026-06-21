package petrolpark.mc.library.core.client.effectShaders;

import petrolpark.mc.library.registry.PetrolparkPostUniforms;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class ClientEffectHandler {
    public static void updateUniforms(float value) {
        PetrolparkPostUniforms.set("EffectFactor", uniform -> uniform.set(value));
    }
}
