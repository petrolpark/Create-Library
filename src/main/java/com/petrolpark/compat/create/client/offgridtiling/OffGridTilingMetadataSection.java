package com.petrolpark.compat.create.client.offgridtiling;

import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record OffGridTilingMetadataSection(float xSize, float ySize, float scale) {
    
    public static final Codec<OffGridTilingMetadataSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("x").forGetter(OffGridTilingMetadataSection::xSize), // The width of the tiling pattern proportional to the width of a block
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("y").forGetter(OffGridTilingMetadataSection::ySize), // The height of the tiling pattern proportional to the height of a block
        ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(OffGridTilingMetadataSection::scale) // The size of the texture proportional to the UV size of a full block side (which is typically 16x16 pixels)
    ).apply(instance, OffGridTilingMetadataSection::new)).validate(section -> {
        if (section.xSize() == 0f || section.ySize() == 0f || section.scale() == 0f) return DataResult.error(() -> "Off-grid-tiling fields may not be 0");
        return DataResult.success(section);
    });

    public static final String SECTION_NAME = Petrolpark.asResource("off_grid_tiling").toString();

    public static final MetadataSectionType<OffGridTilingMetadataSection> TYPE = MetadataSectionType.fromCodec(SECTION_NAME, CODEC);

    public static final Set<MetadataSectionSerializer<?>> DEFAULT_METADATA_SERIALIZERS_WITH_OGT = Set.of(AnimationMetadataSection.SERIALIZER, TYPE);
};
