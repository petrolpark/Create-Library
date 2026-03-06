package com.petrolpark.core.registrate;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class PetrolparkTagGen {

	public static final Multimap<ResourceLocation, TagKey<Block>> UNREQUIRED_BLOCKS = MultimapBuilder.hashKeys().arrayListValues().build();
	public static final Multimap<ResourceLocation, TagKey<Fluid>> UNREQUIRED_FLUIDS = MultimapBuilder.hashKeys().arrayListValues().build();
    public static final Multimap<ResourceLocation, TagKey<Item>> UNREQUIRED_ITEMS = MultimapBuilder.hashKeys().arrayListValues().build();

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE);
	};

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
	};

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
		return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
	};
};
