package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.mixin.accessor.AbstractBuilderAccessor;
import com.tterrag.registrate.builders.FluidBuilder;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.level.material.Fluid;

@Mixin(FluidBuilder.class)
public class FluidBuilderMixin {
    
    /**
     * Temporary fix for https://github.com/tterrag1098/Registrate/issues/81
     */
    @WrapOperation(
        method = "Lcom/tterrag/registrate/builders/FluidBuilder;lambda$tag$25(Lnet/minecraft/data/tags/TagsProvider$TagAppender;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/data/tags/TagsProvider$TagAppender;add(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/data/tags/TagsProvider$TagAppender;"
        )
    )
    public TagAppender<Fluid> petrolpark$sourceTagsAlsoOptional(TagAppender<Fluid> tagAppender, ResourceKey<Fluid> key, Operation<TagAppender<Fluid>> original) {
        return ((AbstractBuilderAccessor)this).getIsOptional() ? tagAppender.add(TagEntry.optionalElement(key.location())) : original.call(tagAppender, key);
    };
};
