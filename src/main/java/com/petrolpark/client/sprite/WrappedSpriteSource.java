package com.petrolpark.client.sprite;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class WrappedSpriteSource implements SpriteSource {

    private final SpriteSource wrappedSource;
    private final String prefix;

    public WrappedSpriteSource(SpriteSource wrappedSource, String prefix) {
        this.wrappedSource = wrappedSource;
        this.prefix = prefix;
    };

    public SpriteSource getWrappedSource() {
        return wrappedSource;
    };

    public String getPrefix() {
        return prefix;
    };

    @Override
    public void run(@Nonnull ResourceManager resourceManager, @Nonnull SpriteSource.Output output) {
        final SpriteResourceLoader resourceLoader = createSpriteResourceLoader();
        final SpriteSource.Output wrappedOutput = new SpriteSource.Output() {

            @Override
            public void add(@Nonnull ResourceLocation location, @Nonnull SpriteSupplier sprite) {
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
