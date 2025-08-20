package com.petrolpark;

import com.mojang.blaze3d.shaders.AbstractUniform;

import java.util.function.Consumer;

public enum PetrolparkPostUniforms {
    EFFECT_FACTOR("EffectFactor");

    final String name;
    Consumer<AbstractUniform> onUpdate;

    PetrolparkPostUniforms(String name, Consumer<AbstractUniform> onUpdate) {
        this.name = name;
        this.onUpdate = onUpdate;
    }

    PetrolparkPostUniforms(String name) {
        this.name = name;
        this.onUpdate = (uniform) -> {};
    }

    public void update(Consumer<AbstractUniform> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void applyUniform(AbstractUniform uniform) {
        this.onUpdate.accept(uniform);
    }

    public String getName() {
        return this.name;
    }
}
