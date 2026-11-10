package petrolpark.mc.library.core.client.effectShaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import petrolpark.mc.library.Petrolpark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShaderEffectReloadHandler {
    private static final Map<IShaderEffect, PostChain> shaderCache = new HashMap<>();
    private static boolean ready = false;

    public static void prepareShaders(ResourceManager pResourceManager) {
        ShaderEffectReloadHandler.clearCache();
        SharedTargetsHandler.putTarget("default");
        Minecraft mc = Minecraft.getInstance();

        for ( MobEffect effect : BuiltInRegistries.MOB_EFFECT ) {
            if ( effect instanceof IShaderEffect shaderEffect ) {
                ResourceLocation location = shaderEffect.getShader();
                if ( location != null && ! ShaderEffectReloadHandler.hasShader(shaderEffect) ) {
                    ShaderEffectReloadHandler.createShader(shaderEffect, mc, pResourceManager);
                }
            }
        }
        Petrolpark.LOGGER.info("All shader effects preloaded.");
        ready = true;
    }

    public static void createShader(IShaderEffect shaderEffect, Minecraft minecraft, ResourceManager manager) {
        ResourceLocation location = rlForShader(shaderEffect.getShader());

        try {
            SharedTargetsHandler.loadTargetsFor(shaderEffect.getShader(), manager);
            PostChain chain = new PostChain(minecraft.getTextureManager(), manager, minecraft.getMainRenderTarget(), location);
            chain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            shaderCache.put(shaderEffect, chain);
        } catch (IOException exception ) {
            Petrolpark.LOGGER.error("Failed to preload shader: {}", location);
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
        SharedTargetsHandler.clear();
        ready = false;
    };

    private static ResourceLocation rlForShader(ResourceLocation rl) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "shaders/post/" + rl.getPath() + ".json");
    };

    public static boolean isReady() {
        return ready;
    }
};
