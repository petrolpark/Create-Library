package com.petrolpark.client.sprite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MeanShiftClusterSpriteSource extends WrappedSpriteSource {

    public static final MapCodec<MeanShiftClusterSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SpriteSources.CODEC.fieldOf("source").forGetter(MeanShiftClusterSpriteSource::getWrappedSource),
        Codec.STRING.optionalFieldOf("prefix", "").forGetter(MeanShiftClusterSpriteSource::getPrefix),
        Codec.intRange(0, 1024).optionalFieldOf("max_iterations", 20).forGetter(MeanShiftClusterSpriteSource::getMaxIterations),
        Codec.doubleRange(1d, 255d).optionalFieldOf("min_bandwidth", 30d).forGetter(MeanShiftClusterSpriteSource::getMinBandwidth)
    ).apply(instance, MeanShiftClusterSpriteSource::new));

    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);

    private final int maxIterations;
    private final double minBandwidth;

    public MeanShiftClusterSpriteSource(SpriteSource wrapped, String prefix, int maxIterations, double minBandwidth) {
        super(wrapped, prefix);
        this.maxIterations = maxIterations;
        this.minBandwidth = minBandwidth;
    };

    public int getMaxIterations() {
        return maxIterations;
    };

    public double getMinBandwidth() {
        return minBandwidth;
    };

    @Override
    public SpriteSupplier transform(SpriteContents original) {
        final NativeImage originalImage = original.getOriginalImage();

        final double bandwidth = minBandwidth; //TODO bandwith estimation
        
        final Map<Integer, Integer> weightedColors = new HashMap<>();
        for (int pixel : originalImage.getPixelsRGBA()) weightedColors.merge(pixel, 1, Integer::sum);
        final Set<Integer> visitedColors = new HashSet<>(weightedColors.size());
        final Map<Integer, Integer> colorMap = new HashMap<>(weightedColors.size());

        startEachColor: for (Map.Entry<Integer, Integer> startWeightedColor : weightedColors.entrySet()) {
            if (visitedColors.contains(startWeightedColor.getKey())) continue startEachColor;

            Vec3 clusterCenter = toVec(startWeightedColor.getKey());
            final Set<Integer> includedColors = new HashSet<>();

            iterateCluster: for (int iteration = 0; iteration < maxIterations; iteration++) {
                includedColors.clear();
                Vec3 totalColor = Vec3.ZERO;
                int totalWeight = 0;
                for (Map.Entry<Integer, Integer> weightedColor : weightedColors.entrySet()) {
                    final Vec3 color = toVec(weightedColor.getKey());
                    if (clusterCenter.distanceToSqr(color) > bandwidth) continue iterateCluster;
                    includedColors.add(weightedColor.getKey());
                    final int weight = weightedColor.getValue();
                    totalColor = totalColor.add(color).scale(weight);
                    totalWeight += weight;
                };
                if (totalWeight <= 0) continue iterateCluster;
                Vec3 oldClusterCenter = clusterCenter;
                clusterCenter = totalColor.scale(1d / (double)totalWeight);
                if (oldClusterCenter.distanceToSqr(clusterCenter) < 1d) break iterateCluster;
            };

            final int clusterCenterRGB = toRGB(clusterCenter);
            includedColors.forEach(color -> colorMap.put(color, clusterCenterRGB));
            visitedColors.addAll(includedColors);
        };

        return new SpriteSupplier() {

            @Override
            public SpriteContents apply(SpriteResourceLoader resourceLoader) {
                return new SpriteContents(
                    original.name(), new FrameSize(original.width(), original.height()), 
                    originalImage.mappedCopy(originalRGB -> 
                        (originalRGB & 0xFF000000) | (colorMap.get(originalRGB) & 0x00FFFFFF) // Alpha of original pixel, and color of mean shift cluster
                    ),
                    original.metadata()
                );
            };

            @Override
            public void discard() {
                originalImage.close();
            };
            
        };
    };

    private static final Vec3 toVec(int color) {
        return new Vec3((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
    };

    private static final int toRGB(Vec3 color) {
        return (0xFF << 24) | (Mth.clamp((int)color.x(), 0, 255) << 16) | (Mth.clamp((int)color.y(), 0, 255) << 8) | Mth.clamp((int)color.z(), 0, 255);
    };

    @Override
    public SpriteSourceType type() {
        return TYPE;
    };
    
};
