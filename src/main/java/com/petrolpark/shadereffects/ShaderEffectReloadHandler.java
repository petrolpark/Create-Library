package com.petrolpark.shadereffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderEffectReloadHandler {

    private static final Map<ResourceLocation, PostChain> shaderCache = new HashMap<>();
    public static final Set<IShaderEffect> forbiddenEffects = new HashSet<>();

    public static void createShader(ResourceLocation location, IShaderEffect shaderEffect, Minecraft minecraft, ResourceManager manager) {
        if (shaderCache.containsKey(location)) {
            System.err.println("[Petrolpark] Cannot load shader location two times; Putting " + shaderEffect.getClass().getName() + " in forbidden effects");
            forbiddenEffects.add(shaderEffect);
            return;
        }

        try {
            PostChain chain = new PostChain(minecraft.textureManager, manager, minecraft.getMainRenderTarget(), location);
            chain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            shaderCache.put(location, chain);
        } catch ( IOException exception ) {
            System.err.println("[Petrolpark] Failed to preload shader: " + location);
            exception.printStackTrace();
        }
    }

    public static PostChain getShader(ResourceLocation location) {
        return shaderCache.get(location);
    }

    public static boolean hasShader(ResourceLocation location) {
        return shaderCache.containsKey(location);
    }

    public static void clearCache() {
        shaderCache.values().forEach(PostChain::close);
        shaderCache.clear();
    }

    public static boolean hasForbiddenEffect(MobEffect effect) {
        return forbiddenEffects.contains((( IShaderEffect ) effect));
    }
}
