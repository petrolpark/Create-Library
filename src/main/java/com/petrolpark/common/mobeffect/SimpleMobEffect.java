package com.petrolpark.common.mobeffect;

import com.petrolpark.Petrolpark;
import com.petrolpark.shadereffect.IShaderEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SimpleMobEffect extends MobEffect implements IShaderEffect {

    public SimpleMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    @Override
    public ResourceLocation getShader() {
        return Petrolpark.asResource("baby_blue_withdrawal");
    }
};
