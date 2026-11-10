package petrolpark.mc.library.core.client.effectShaders;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.registry.PetrolparkPostUniforms;

@OnlyIn( Dist.CLIENT)
public class ClientEffectHandler {
    public static void updateUniforms(int amplifier, float factor) {
        PetrolparkPostUniforms.set(
                PetrolparkPostUniforms.Defaults.EFFECT_FACTOR,
                uniform -> uniform.set(factor)
        );

        PetrolparkPostUniforms.set(
                PetrolparkPostUniforms.Defaults.AMPLIFIER,
                uniform -> uniform.set(amplifier)
        );
    }
}
