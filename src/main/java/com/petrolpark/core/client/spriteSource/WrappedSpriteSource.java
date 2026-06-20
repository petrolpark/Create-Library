package com.petrolpark.core.client.spriteSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Takes an existing {@link SpriteSource} and applies some function to its results,
 * typically a texture transformation like with {@link MeanShiftClusterSpriteSource}.
 */
public abstract class WrappedSpriteSource implements SpriteSource {

    private final SpriteSource wrappedSource;
    private final String prefix;
    private final Optional<List<String>> allowedNamespaces;

    public WrappedSpriteSource(SpriteSource wrappedSource, String prefix, Optional<List<String>> allowedNamespaces) {
        this.wrappedSource = wrappedSource;
        this.prefix = prefix;
        this.allowedNamespaces = allowedNamespaces;
    };

    public SpriteSource getWrappedSource() {
        return wrappedSource;
    };

    public String getPrefix() {
        return prefix;
    };

    public Optional<List<String>> getAllowedNamespaces() {
        return allowedNamespaces;
    };

    @Override
    public void run(@Nonnull ResourceManager resourceManager, @Nonnull SpriteSource.Output output) {
        final SpriteResourceLoader resourceLoader = createSpriteResourceLoader();
        final SpriteSource.Output wrappedOutput = new SpriteSource.Output() {

            @Override
            public void add(@Nonnull ResourceLocation location, @Nonnull SpriteSupplier sprite) {
                if (allowedNamespaces.isPresent() && !allowedNamespaces.get().contains(location.getNamespace())) return;
                output.add(location.withPrefix(prefix), transform(sprite.apply(resourceLoader)));
            };

            @Override
            public void removeAll(@Nonnull Predicate<ResourceLocation> predicate) {
                output.removeAll(predicate);
            };

        };
        wrappedSource.run(resourceManager, wrappedOutput);
    };

    public SpriteResourceLoader createSpriteResourceLoader() {
        return SpriteResourceLoader.create(SpriteLoader.DEFAULT_METADATA_SECTIONS);
    };

    public abstract SpriteSupplier transform(SpriteContents original);
    
};
