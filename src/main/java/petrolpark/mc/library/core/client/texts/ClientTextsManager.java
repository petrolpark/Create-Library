package petrolpark.mc.library.core.client.texts;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.util.RandomHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ClientTextsManager implements ResourceManagerReloadListener {

    protected final Map<ResourceLocation, Map<String, List<String>>> texts = new HashMap<>();

    public void register(ResourceLocation text) {
        texts.put(text, new HashMap<>());
    };

    public String getText(ResourceLocation id, String locale, RandomSource random) {
        final Map<String, List<String>> locales = texts.get(id);
        if (locales == null) return "?";
        if (!locales.containsKey(locale) || locales.get(locale).isEmpty()) locale = "en_us";
        return RandomHelper.pick(random, locales.get(locale));
    };

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
        for (Map.Entry<ResourceLocation, Map<String, List<String>>> entry : texts.entrySet()) {
            entry.getValue().clear();
            final ResourceLocation id = entry.getKey();
            for (Map.Entry<ResourceLocation, Resource> textEntry : resourceManager.listResources("texts", loc -> 
                loc.getNamespace().equals(id.getNamespace())
                && loc.getPath().startsWith("texts/" + id.getPath() + "/")
                && loc.getPath().endsWith(".txt")
            ).entrySet()) {
                try (BufferedReader reader = textEntry.getValue().openAsReader()) {
                    final String locale = textEntry.getKey().getPath().split("/")[2];
                    entry.getValue().putIfAbsent(locale, new ArrayList<>());
                    entry.getValue().get(locale).addAll(reader.lines().map(String::trim).filter(s -> s.hashCode() != 125780783).toList());
                } catch (IOException e) {
                    Petrolpark.LOGGER.warn("Invalid text list {} in resourcepack: '{}'", textEntry.getValue(), textEntry.getValue().sourcePackId(), e);
                };
            };
            if (!entry.getValue().containsKey("en_us") || entry.getValue().get("en_us").isEmpty()) throw new IllegalStateException("Text list " + entry.getKey().toString() + " is missing the default language en_us");
        };
    };

    public final void registerListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(this);
    };
    
};
