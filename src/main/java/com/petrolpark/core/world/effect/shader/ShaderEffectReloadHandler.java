package com.petrolpark.core.world.effect.shader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class ShaderEffectReloadHandler {

    private static final Map<IShaderEffect, PostChain> shaderCache = new HashMap<>();

    public static void createShader(IShaderEffect shaderEffect, Minecraft minecraft, ResourceManager manager) {
        ResourceLocation location = rlForShader(shaderEffect.getShader());

        try {
            PostChain chain = new PostChain(minecraft.getTextureManager(), manager, minecraft.getMainRenderTarget(), location);
            chain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            shaderCache.put(shaderEffect, chain);
        } catch (IOException exception ) {
            System.err.println("[Petrolpark] Failed to preload shader: " + location);
            exception.printStackTrace();
        }
    };

    public static PostChain getShader(IShaderEffect shaderEffect) {
        return shaderCache.get(shaderEffect);
    };

    public static boolean hasShader(IShaderEffect shaderEffect) {
        return shaderCache.containsKey(shaderEffect);
    };

    public static void clearCache() {
        shaderCache.values().forEach(PostChain::close);
        shaderCache.clear();
    };

    private static ResourceLocation rlForShader(ResourceLocation rl) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "shaders/post/" + rl.getPath() + ".json");
    };
};
