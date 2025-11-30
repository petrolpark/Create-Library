package com.petrolpark.core.registrate;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.petrolpark.Petrolpark;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PetrolparkTagGen {

	public static final Multimap<ResourceLocation, TagKey<Block>> UNREQUIRED_BLOCKS = MultimapBuilder.hashKeys().arrayListValues().build();
    
    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
		return b -> b instanceof SharedBlockBuilder ? PetrolparkTagGen.<T, P>tagUnrequired(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE).apply(b) : b.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE);
	};

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
		return b -> b instanceof SharedBlockBuilder ? PetrolparkTagGen.<T, P>tagUnrequired(BlockTags.MINEABLE_WITH_AXE).apply(b) : b.tag(BlockTags.MINEABLE_WITH_AXE);
	};

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
		return b -> b instanceof SharedBlockBuilder ? PetrolparkTagGen.<T, P>tagUnrequired(BlockTags.MINEABLE_WITH_PICKAXE).apply(b) : b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
	};

	@SafeVarargs
	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> tagUnrequired(TagKey<Block> ... tags) {
		return b -> {
			for (TagKey<Block> tag : tags) UNREQUIRED_BLOCKS.put(ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), b.getName()), tag);
			return b;	
		};
	};

	public static class UnrequiredTagsProvider<T> extends TagsProvider<T> {

		public final Multimap<ResourceLocation, TagKey<T>> toAdd;

		public UnrequiredTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper,  Multimap<ResourceLocation, TagKey<T>> toAdd) {
			super(output, registryKey, lookupProvider, Petrolpark.MOD_ID, existingFileHelper);
			this.toAdd = toAdd;
		};

		@Override
		protected void addTags(@Nonnull HolderLookup.Provider provider) {
			toAdd.entries().forEach(entry -> tag(entry.getValue()).addOptional(entry.getKey()));
		};

	};
};
