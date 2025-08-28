package com.petrolpark.client.sprite;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public class SmallBannerSpriteSource implements SpriteSource {

    public static final MapCodec<SmallBannerSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("source").forGetter(source -> source.sourcePath),
        Codec.STRING.fieldOf("prefix").forGetter(source -> source.prefix)
    ).apply(instance, SmallBannerSpriteSource::new));

    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);

    private final String sourcePath;
    private final String prefix;

    private final FileToIdConverter directoryFileToIdConverter;

    public SmallBannerSpriteSource(String sourcePath, String prefix) {
        this.sourcePath = sourcePath;
        this.prefix = prefix;
        directoryFileToIdConverter = new FileToIdConverter("textures/" + this.sourcePath, ".png");
    };

    @Override
    public void run(@Nonnull ResourceManager resourceManager, @Nonnull SpriteSource.Output output) {
        directoryFileToIdConverter.listMatchingResources(resourceManager).forEach((location, resource) -> {
            ResourceLocation id = directoryFileToIdConverter.fileToId(location).withPrefix(prefix);

            try {
                Splicer splicer = resource.metadata().getSection(MetadataSection.TYPE).map(MetadataSection::splicer).orElse(Splicer.TOP_AND_BOTTOM);
                LazyLoadedImage image = new LazyLoadedImage(location, resource, 1);
                output.add(id, splicer.createSpriteSource(id, image));
            } catch (IOException exception) {

            };

            
        });
    };

    @Override
    public SpriteSourceType type() {
        return TYPE;
    };

    public static enum Splicer implements StringRepresentable {
        
        TOP_AND_BOTTOM {
            @Override
            public SpriteSource.SpriteSupplier createSpriteSource(ResourceLocation id, LazyLoadedImage image) {
               return new TopAndBottom(id, image);
            };
        },
        SQUEEZE {
            @Override
            public SpriteSource.SpriteSupplier createSpriteSource(ResourceLocation id, LazyLoadedImage image) {
                return new Squeeze(id, image);
            };
        },
        ;

        public static final Codec<Splicer> CODEC = StringRepresentable.fromEnum(Splicer::values);

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        };

        public abstract SpriteSource.SpriteSupplier createSpriteSource(ResourceLocation id, LazyLoadedImage image);

    };

    protected static final double BANNER_FRONT_HEIGHT = 40 / 64d;
    protected static final double BANNER_FRONT_HALF_HEIGHT = 20 / 64d;
    protected static final double BANNER_FRONT_WIDTH = 22 / 64d;
    protected static final double SMALL_BANNER_HEIGHT = 22 / 64d;

    protected static record TopAndBottom(ResourceLocation id, LazyLoadedImage image) implements SpriteSource.SpriteSupplier {

        @Override
        public SpriteContents apply(SpriteResourceLoader resourceLoader) {
            try {
                NativeImage bigImage = image().get();

                final int halfwayDown = Mth.floor(BANNER_FRONT_HALF_HEIGHT * bigImage.getHeight());
                final int bottom = Mth.floor(BANNER_FRONT_HEIGHT * bigImage.getHeight());
                final int right = Mth.floor(BANNER_FRONT_WIDTH * bigImage.getWidth());
                final int smallBannerHeight = Mth.floor(SMALL_BANNER_HEIGHT * bigImage.getHeight());
                int topHalfTop = 0;
                int bottomHalfBottom = bottom;

                checkRows: while (topHalfTop < halfwayDown) {
                    for (int x = 0; x < right; x++) {
                        if (bigImage.getLuminanceOrAlpha(x, topHalfTop) != 0) break checkRows; // If this row of pixels is not empty
                    };
                    topHalfTop++;
                };

                checkRows: while (bottomHalfBottom > halfwayDown + 1) {
                    for (int x = 0; x < right; x++) {
                        if (bigImage.getLuminanceOrAlpha(x, bottomHalfBottom) != 0) break checkRows; // If this row of pixels is not empty
                    };
                    bottomHalfBottom--;
                };

                NativeImage smallImage = new NativeImage(NativeImage.Format.RGBA, bigImage.getWidth() / 2, bigImage.getHeight() / 2, false);
                
                if (bottomHalfBottom - topHalfTop <= smallBannerHeight) {
                    if (topHalfTop == 0) {
                        bottomHalfBottom = smallBannerHeight;
                    } else if (bottomHalfBottom == bottom) {
                        topHalfTop = bottom - smallBannerHeight;
                    } else {
                        final int padding = smallBannerHeight - bottomHalfBottom + topHalfTop;
                        int topPadding = padding / 2;
                        if (topPadding > topHalfTop) topPadding = topHalfTop;
                        else if (padding - topPadding > bottom - bottomHalfBottom) topPadding = padding - bottom + bottomHalfBottom;
                        topHalfTop -= topPadding;
                        bottomHalfBottom += (padding - topPadding); 
                    };
                    bigImage.copyRect(smallImage, 0, topHalfTop, 0, 0, right, bottomHalfBottom - topHalfTop, false, false);
                } else {
                    final int smallBannerTopHalfHeight = smallBannerHeight / 2;
                    bigImage.copyRect(smallImage, 0, topHalfTop, 0, 0, right, topHalfTop + smallBannerTopHalfHeight, false, false);
                    bigImage.copyRect(smallImage, 0, bottomHalfBottom + 1 - smallBannerHeight + smallBannerTopHalfHeight, 0, smallBannerTopHalfHeight, right, smallBannerHeight - smallBannerTopHalfHeight, false, false);
                };

                return new SpriteContents(id(), new FrameSize(smallImage.getWidth(), smallImage.getHeight()), smallImage, ResourceMetadata.EMPTY);

            } catch (Exception exception) {
                Petrolpark.LOGGER.error("Failed to generate small Banner Pattern {}", id(), exception);
            } finally {
                image().release();
            };

            return MissingTextureAtlasSprite.create();
        };

    };

    protected static record Squeeze(ResourceLocation id, LazyLoadedImage image) implements SpriteSource.SpriteSupplier {

        @Override
        public SpriteContents apply(SpriteResourceLoader resourceLoader) {
            try {
                NativeImage nativeImage = image().get();
                //TODO
            } catch (Exception exception) {

            } finally {
                image().release();
            };

            return MissingTextureAtlasSprite.create();
        };

    };

    protected static record MetadataSection(SmallBannerSpriteSource.Splicer splicer) {
        public static final Codec<MetadataSection> CODEC = CodecHelper.singleField(Splicer.CODEC, "splicer", MetadataSection::splicer, MetadataSection::new);
    
        public static final String SECTION_NAME = Petrolpark.asResource("banner_splicer").toString();

        public static final MetadataSectionType<MetadataSection> TYPE = MetadataSectionType.fromCodec(SECTION_NAME, CODEC);
    };
    
};
