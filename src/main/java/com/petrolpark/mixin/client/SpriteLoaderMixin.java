package com.petrolpark.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.petrolpark.compat.create.client.offgridtiling.OffGridTilingMetadataSection;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {
    
    @ModifyArg(
        method = "loadAndStitch",
        at = @At(
            value = "INVOKE",
            target = "loadAndStitch"
        ),
        index = 4
    )
    public Collection<MetadataSectionSerializer<?>> modifyDefaultSectionSerializers(Collection<MetadataSectionSerializer<?>> metadataSerializers) {
        return OffGridTilingMetadataSection.DEFAULT_METADATA_SERIALIZERS_WITH_OGT;
    };
};
