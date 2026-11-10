package petrolpark.mc.library.registry;

import com.mojang.blaze3d.shaders.AbstractUniform;
import net.minecraft.client.renderer.EffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PetrolparkPostUniforms {
    private static final HashMap<String, Consumer<AbstractUniform>> UNIFORMS = new HashMap<>();

    public static void set(String name, Consumer<AbstractUniform> uniform) {
        UNIFORMS.put(name, uniform);
    }

    public static void set(Defaults uniformName, Consumer<AbstractUniform> uniform) {
        set(uniformName.getId(), uniform);
    }

    public static void apply(@NotNull EffectInstance effectInstance) {
        for (Map.Entry<String, Consumer<AbstractUniform>> uniform : UNIFORMS.entrySet()){
            AbstractUniform shaderUniform = effectInstance.safeGetUniform(uniform.getKey());
            uniform.getValue().accept(shaderUniform);
        }
    }

    public enum Defaults {

        AMPLIFIER("Amplifier"),
        EFFECT_FACTOR("EffectFactor");

        private final String uniformId;
        Defaults(String id) {
            this.uniformId = id;
        }

        public String getId() {
            return uniformId;
        }
    };
}
