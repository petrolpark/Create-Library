package com.petrolpark.core.client.spriteSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.ColorHelper;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.world.phys.Vec3;

/**
 * 
 */
public class MeanShiftClusterSpriteSource extends WrappedSpriteSource {

    public static final MapCodec<MeanShiftClusterSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SpriteSources.CODEC.fieldOf("source").forGetter(MeanShiftClusterSpriteSource::getWrappedSource),
        Codec.STRING.optionalFieldOf("prefix", "").forGetter(MeanShiftClusterSpriteSource::getPrefix),
        Codec.STRING.listOf().optionalFieldOf("allowed_namespaces").forGetter(MeanShiftClusterSpriteSource::getAllowedNamespaces),
        Codec.intRange(0, 1024).optionalFieldOf("max_iterations", 20).forGetter(MeanShiftClusterSpriteSource::getMaxIterations),
        Codec.doubleRange(0d, 1024d).optionalFieldOf("bandwidth_coefficient", 1d).forGetter(MeanShiftClusterSpriteSource::getBandwidthCoefficient),
        Codec.doubleRange(0d, 1d).optionalFieldOf("bandwidth_variation_exponent", 0.5d).forGetter(MeanShiftClusterSpriteSource::getBandwidthVariationExponent),
        Codec.doubleRange(-1d, 0d).optionalFieldOf("bandwidth_sample_size_exponent", -0.142857142857d).forGetter(MeanShiftClusterSpriteSource::getBandwidthSampleSizeExponent)
    ).apply(instance, MeanShiftClusterSpriteSource::new));

    public static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);

    private final int maxIterations;
    private final double bandwidthCoefficient;
    private final double bandwidthVariationExponent;
    private final double bandwidthSampleSizeExponent;

    public MeanShiftClusterSpriteSource(SpriteSource wrappedSource, String prefix, Optional<List<String>> allowedNamespaces, int maxIterations, double bandwidthCoefficient, double bandwidthVariationExponent, double bandwidthSampleSizeExponent) {
        super(wrappedSource, prefix, allowedNamespaces);
        this.maxIterations = maxIterations;
        this.bandwidthCoefficient = bandwidthCoefficient;
        this.bandwidthVariationExponent = bandwidthVariationExponent;
        this.bandwidthSampleSizeExponent = bandwidthSampleSizeExponent;
    };

    public int getMaxIterations() {
        return maxIterations;
    };

    public double getBandwidthCoefficient() {
        return bandwidthCoefficient;
    };
 
    public double getBandwidthVariationExponent() {
        return bandwidthVariationExponent;
    };

    public double getBandwidthSampleSizeExponent() {
        return bandwidthSampleSizeExponent;
    };

    @Override
    public SpriteSupplier transform(SpriteContents original) {
        final NativeImage originalImage = original.getOriginalImage();
        
        final Map<Integer, Integer> weightedColors = new HashMap<>();
        for (final int color : originalImage.getPixelsRGBA()) {
            if (ColorHelper.isFullyTransparent(color)) continue; // Ignore completely empty pixels
            weightedColors.merge(ColorHelper.opaque(color), 1, Integer::sum);
        };

        final Map<Integer, Vec3> OKLabcolors = weightedColors.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> ColorHelper.toOKLabVec(e.getKey())
        ));
        Vec3 totalColor = Vec3.ZERO;
        int totalWeight = 0;
        for (final Map.Entry<Integer, Integer> weightedColor : weightedColors.entrySet()) {
            totalColor = totalColor.add(OKLabcolors.get(weightedColor.getKey()).scale(weightedColor.getValue()));
            totalWeight += weightedColor.getValue();
        };
        final Vec3 meanColor = totalColor.scale(1d / (double)totalWeight);
        final int meanColorRGB = ColorHelper.toRGB(meanColor);
        double sumSquares = 0d;
        for (final Vec3 color : OKLabcolors.values()) {
            sumSquares += color.distanceToSqr(meanColor);
        };
        final double variance = sumSquares / (double)totalWeight;
        final double bandwidth = bandwidthCoefficient * Math.pow(variance, bandwidthVariationExponent) * Math.pow(totalWeight, bandwidthSampleSizeExponent);
        final double bandwidthSquare = bandwidth * bandwidth;

        final Set<Integer> visitedColors = new HashSet<>(weightedColors.size());
        final Map<Integer, Integer> colorMap = new HashMap<>(weightedColors.size());

        while (visitedColors.size() < weightedColors.size()) { // Repeat until all colors have been clustered
            startEachColor: for (final Map.Entry<Integer, Integer> startWeightedColor : weightedColors.entrySet()) {
                if (visitedColors.contains(startWeightedColor.getKey())) continue startEachColor;

                Vec3 clusterCenter = ColorHelper.toOKLabVec(startWeightedColor.getKey());
                final Set<Integer> includedColors = new HashSet<>();

                iterateCluster: for (int iteration = 0; iteration < maxIterations; iteration++) {
                    includedColors.clear();
                    Vec3 totalClusterColor = Vec3.ZERO;
                    int totalClusterWeight = 0;
                    findColorsInCluster: for (final Map.Entry<Integer, Integer> weightedColor : weightedColors.entrySet()) {
                        final Vec3 color = ColorHelper.toOKLabVec(weightedColor.getKey());
                        if (
                            (clusterCenter.distanceToSqr(color) > bandwidthSquare || visitedColors.contains(weightedColor.getKey())) // Don't include colors too far away or that are already in clusters
                            //&& !(startWeightedColor != weightedColor) // Always include the start color
                        ) continue findColorsInCluster;
                        includedColors.add(weightedColor.getKey());
                        final int weight = weightedColor.getValue();
                        totalClusterColor = totalClusterColor.add(color.scale(weight));
                        totalClusterWeight += weight;
                    };
                    if (totalClusterWeight <= 0) continue iterateCluster;
                    final Vec3 oldClusterCenter = clusterCenter;
                    clusterCenter = totalClusterColor.scale(1d / (double)totalClusterWeight);
                    if (oldClusterCenter.distanceToSqr(clusterCenter) < 1 / 255d) break iterateCluster;
                };

                final int clusterCenterRGB = ColorHelper.toRGB(clusterCenter);
                includedColors.forEach(color -> colorMap.put(color, clusterCenterRGB));
                visitedColors.addAll(includedColors);
            };
        };

        return new SpriteSupplier() {

            @Override
            public SpriteContents apply(SpriteResourceLoader resourceLoader) {
                return new SpriteContents(
                    original.name(), new FrameSize(original.width(), original.height()), 
                    originalImage.mappedCopy(originalARGB -> {
                        if (ColorHelper.isFullyTransparent(originalARGB)) return originalARGB;
                        Integer mappedColorRGB = colorMap.get(ColorHelper.opaque(originalARGB));
                        if (mappedColorRGB == null) mappedColorRGB = meanColorRGB; // Just in case of freak accident, to avoid crash when auto-unboxing
                        return ColorHelper.copyAlpha(mappedColorRGB, originalARGB);
                    }),
                    original.metadata()
                );
            };

            @Override
            public void discard() {
                originalImage.close();
            };
            
        };
    };

    @Override
    public SpriteSourceType type() {
        return TYPE;
    };
    
};
