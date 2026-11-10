package petrolpark.mc.library.core.client.effectShaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import petrolpark.mc.library.Petrolpark;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class SharedTargetsHandler {
    private static final Object2ReferenceOpenHashMap<String, NonDestructibleTextureTarget> sharedTargets = new Object2ReferenceOpenHashMap<>();
    private static final HashMap<String, List<String>> targetsDefinitionsCache = new HashMap<>();
    private static final HashMap<String, MutableInt> loadedTargets = new HashMap<>();

    public static TextureTarget getTarget(String name) {
        TextureTarget target = sharedTargets.get(name);

        if (target == null) throw new NoSuchElementException("No shared target with name " + name + " was declared.");

        return target;
    }

    public static boolean hasTarget(String name) {
        return sharedTargets.containsKey(name);
    }

    public static void resizeTargets(Window window) {
        sharedTargets.values().forEach(target -> {
            target.resize(window.getWidth(), window.getHeight(), true);
        });
    }

    public static void putTarget(String name) {
        if (sharedTargets.containsKey(name)) return;

        Window window = Minecraft.getInstance().getWindow();
        NonDestructibleTextureTarget target = new NonDestructibleTextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX);
        target.setClearColor(0, 0, 0, 0);

        sharedTargets.put(name, target);
    }

    public static void beginFrame() {
        sharedTargets.values().forEach((textureTarget -> textureTarget.clear(Minecraft.ON_OSX)));
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    public static void copyDepthFromMain() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        sharedTargets.get("default").copyDepthFrom(mainTarget);
        mainTarget.bindWrite(true);
    }

    public static void loadTargetsFor(ResourceLocation shader, ResourceManager manager) {
        if (!targetsDefinitionsCache.containsKey(shader.toString())) {
            String namespace = shader.getNamespace();
            ResourceLocation targetDefinitions = ResourceLocation.fromNamespaceAndPath(namespace, "shaders/shared_targets.json");
            Optional<Resource> resourceOpt = manager.getResource(targetDefinitions);

            if (resourceOpt.isEmpty()) {
                Petrolpark.LOGGER.info("No shared targets definitions found for {}. Skipping.", shader.getNamespace());
                return;
            }

            try (Reader reader = resourceOpt.get().openAsReader()){
                JsonObject jsonobject = GsonHelper.parse(reader);
                jsonobject.asMap().forEach((effectName, element) -> {
                    List<String> rendertypes = element.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
                    Petrolpark.LOGGER.info("Caching {} called by {} with rendertypes {}", namespace + ":" + effectName, shader, rendertypes);
                    if (rendertypes.isEmpty()) return;
                    targetsDefinitionsCache.put(namespace + ":" + effectName, rendertypes);
                });
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        }

        targetsDefinitionsCache.get(shader.toString()).forEach(SharedTargetsHandler::putTarget);
    }

    public static void clear() {
        sharedTargets.values().forEach(NonDestructibleTextureTarget::closeTexture);
        sharedTargets.clear();
        targetsDefinitionsCache.clear();
        loadedTargets.clear();
    }

    public static boolean shouldBeLoaded(String name) {
        return loadedTargets.containsKey(name);
    }

    public static void decrementLoadedTargetsCounter(String id) {
        if (!targetsDefinitionsCache.containsKey(id)) return;
        for (String targetDefinition : targetsDefinitionsCache.get(id)) {
            if (!loadedTargets.containsKey(targetDefinition)) Petrolpark.LOGGER.error("Could not find target [{}] in loaded targets cache", targetDefinition);

            MutableInt count = loadedTargets.get(targetDefinition);
            if (count.decrementAndGet() == 0) {
                loadedTargets.remove(targetDefinition);
            }
        }
    }

    public static void incrementLoadedTargetsCounter(String id) {
        if (!targetsDefinitionsCache.containsKey(id)) return;
        for (String targetDefinition : targetsDefinitionsCache.get(id)) {
            if (loadedTargets.containsKey(targetDefinition)) {
                loadedTargets.get(targetDefinition).increment();
            } else {
                loadedTargets.put(targetDefinition, new MutableInt(1));
            }
        }
    }
}
