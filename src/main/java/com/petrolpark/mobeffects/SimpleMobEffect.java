package com.petrolpark.mobeffects;

import com.petrolpark.Petrolpark;
import com.petrolpark.shadereffects.IShaderEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SimpleMobEffect extends MobEffect implements IShaderEffect {

    protected SimpleMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    @Override
    public ResourceLocation getShader() {
        return Petrolpark.asResource("shaders/post/lead_poisoning.json");
    }
};
