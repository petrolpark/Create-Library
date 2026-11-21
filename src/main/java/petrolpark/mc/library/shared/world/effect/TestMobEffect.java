package petrolpark.mc.library.shared.world.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.client.effectShaders.IShaderEffect;
import petrolpark.mc.library.core.world.effect.SimpleMobEffect;

public class TestMobEffect extends SimpleMobEffect implements IShaderEffect {
    public TestMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public ResourceLocation getShader() {
        return Petrolpark.asResource("test_shader");
    }
}
