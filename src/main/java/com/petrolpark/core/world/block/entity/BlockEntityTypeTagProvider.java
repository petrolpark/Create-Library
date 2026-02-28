package com.petrolpark.core.world.block.entity;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockEntityTypeTagProvider extends IntrinsicHolderTagsProvider<BlockEntityType<?>> {

    @SuppressWarnings("null")
    public BlockEntityTypeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK_ENTITY_TYPE, lookupProvider, type -> type.builtInRegistryHolder().getKey(), modId, existingFileHelper);
    };

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {};
    
    @Override
    public IntrinsicTagAppender<BlockEntityType<?>> tag(@Nonnull TagKey<BlockEntityType<?>> tag) {
        return super.tag(tag);
    };
};
