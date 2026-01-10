package com.petrolpark.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.client.rendering.BakedModelHelper;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.render.StitchedSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Largely copied from {@link WaterWheelRenderer}.
 */
public class WoodHelper {

    public static final Wood OAK = new Wood("minecraft", "oak");
    
    @OnlyIn(Dist.CLIENT)
    public static final StitchedSprite 
    PLANKS_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_planks")),
	LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log")),
    LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log_top")),
    STRIPPED_LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/stripped_oak_log")),
    STRIPPED_LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/stripped_oak_log_top")),
    DOOR_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_door_top")),
    DOOR_BOTTOM_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_door_bottom")),
    TRAPDOOR_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_trapdoor"));

    @OnlyIn(Dist.CLIENT)
    public static final Component getName(@Nullable Wood wood) {
        if (wood == null) return Lang.generic("wood.unknown");
        return getPlanksBlock(wood)
            .map(Block::getName)
            .map(MutableComponent::getString)
            .map(Pattern.compile(Lang.generic("wood.planks_regex").getString())::matcher)
            .filter(Matcher::find)
            .map(matcher -> matcher.group(1))
            .<Component>map(Component::literal)
            .orElse(Lang.generic("wood.unknown"));
    };

    public static final Stream<Wood> streamAllWoods() {
        return StreamSupport.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.PLANKS)
            .spliterator(), false
            ).map(Holder::value)
            .map(WoodHelper::getWoodFromPlanksBlock);
    };

    @OnlyIn(Dist.CLIENT)
    public static final BakedModel generateWoodModel(BakedModel template, Wood wood) {
		if (wood == null || OAK.equals(wood)) return BakedModelHelper.swapSprites(template, UnaryOperator.identity());

		final BlockState logState = getLogBlockOrOak(wood).defaultBlockState();
        final BlockState strippedLogState = getStrippedLogBlockOrOak(wood).defaultBlockState();
        final BlockState doorBottomState = getDoorBlockOrOak(wood).defaultBlockState();

		final Map<TextureAtlasSprite, TextureAtlasSprite> map = new Reference2ReferenceOpenHashMap<>();
		map.put(PLANKS_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(getPlanksBlockOrOak(wood).defaultBlockState(), Direction.UP));
		map.put(LOG_SIDE_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(logState, Direction.SOUTH));
		map.put(LOG_TOP_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(logState, Direction.UP));
        map.put(STRIPPED_LOG_SIDE_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(strippedLogState, Direction.SOUTH));
		map.put(STRIPPED_LOG_TOP_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(strippedLogState, Direction.UP));
        map.put(DOOR_BOTTOM_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(doorBottomState, Direction.SOUTH));
        map.put(DOOR_TOP_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(doorBottomState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Direction.SOUTH));
        map.put(TRAPDOOR_TEMPLATE.get(), BakedModelHelper.getSpriteOnSide(getTrapdoorBlockOrOak(wood).defaultBlockState(), Direction.UP));

		return BakedModelHelper.swapSprites(template, map::get);
	};

    public static final Optional<Block> getPlanksBlock(Wood wood) {
        final Optional<Block> planksBlock = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), wood.name() + "_planks")).map(Holder::value);
        if (planksBlock.isPresent()) return planksBlock;
        return BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), "wood/planks/"+wood.name())).map(Holder::value);
    };

    public static final Block getPlanksBlockOrOak(Wood wood) {
        return getPlanksBlock(wood).orElse(Blocks.OAK_PLANKS);
    };

    @Nullable
    public static final Wood getWoodFromPlanks(Object planks) {
        return getWoodFromPlanksBlock(BlockHelper.getBlock(planks));
    };

    @Nullable
    public static final Wood getWoodFromPlanksBlock(Block planksBlock) {
        if (planksBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(planksBlock);
		final String path = id.getPath();

		if (path.endsWith("_planks")) // Covers most wood types
			return new Wood(id.getNamespace(), path.substring(0, path.length() - 7));

		if (path.contains("wood/planks/")) // TerraFirmaCraft
			return new Wood(id.getNamespace(), path.substring(12));

		return null;
    };

    @Nullable
    public static final Wood getWoodFromSlab(Object slab) {
        return getWoodFromSlabBlock(BlockHelper.getBlock(slab));
    };

    @Nullable
    public static final Wood getWoodFromSlabBlock(Block slabBlock) {
        if (slabBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(slabBlock);
		String path = id.getPath();

        if (path.endsWith("_slab")) {
            path = path.substring(0, path.length() - 5);

            if (path.contains("wood/planks/")) path = path.substring(12); // TFC

            return new Wood(id.getNamespace(), path);
        };

        return null;
    };

    @Nullable
    public static final Wood getWoodFromStairs(Object stairs) {
        return getWoodFromStairsBlock(BlockHelper.getBlock(stairs));
    };

    @Nullable
    public static final Wood getWoodFromStairsBlock(Block stairsBlock) {
        if (stairsBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(stairsBlock);
		String path = id.getPath();

        if (path.endsWith("_stairs")) {
            path = path.substring(0, path.length() - 7);

            if (path.contains("wood/planks/")) path = path.substring(12); // TFC

            return new Wood(id.getNamespace(), path);
        };

        return null;
    };

    @Nullable
    public static final Wood getWoodFromFence(Object fence) {
        return getWoodFromFenceBlock(BlockHelper.getBlock(fence));
    };

    @Nullable
    public static final Wood getWoodFromFenceBlock(Block fenceBlock) {
        if (fenceBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(fenceBlock);
		String path = id.getPath();

        if (path.endsWith("_fence")) 
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 6));

        if (path.contains("wood/fence/")) // TFC
            return new Wood(id.getNamespace(), path.substring(11));
        
        return null;
    };

    @Nullable
    public static final Wood getWoodFromFenceGate(Object fenceGate) {
        return getWoodFromFenceGateBlock(BlockHelper.getBlock(fenceGate));
    };

    @Nullable
    public static final Wood getWoodFromFenceGateBlock(Block fenceGateBlock) {
        if (fenceGateBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(fenceGateBlock);
		String path = id.getPath();

        if (path.endsWith("_fence_gate")) 
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 11));

        if (path.contains("wood/fence_gate/")) // TFC
            return new Wood(id.getNamespace(), path.substring(16));
        
        return null;
    };

    @Nullable
    public static final Wood getWoodFromButton(Object button) {
        return getWoodFromButtonBlock(BlockHelper.getBlock(button));
    };

    @Nullable
    public static final Wood getWoodFromButtonBlock(Block buttonBlock) {
        if (buttonBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(buttonBlock);
		String path = id.getPath();

        if (path.endsWith("_button")) 
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 7));

        if (path.contains("wood/button/")) // TFC
            return new Wood(id.getNamespace(), path.substring(12));
        
        return null;
    };

    @Nullable
    public static final Wood getWoodFromLog(Object log) {
        return getWoodFromLogBlock(BlockHelper.getBlock(log));
    };

    @Nullable
    public static final Wood getWoodFromLogBlock(Block logBlock) {
        if (logBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(logBlock);
		String path = id.getPath();

        if (path.contains("wood/log/"))  // TFC
            return new Wood(id.getNamespace(), path.substring(9));

        boolean found = false;

        if (path.endsWith("_log")) {
            path = path.substring(0, path.length() - 4);
            found = true;
        };

        if (path.endsWith("_stem")) {
            path = path.substring(0, path.length() - 5);
            found = true;
        };
        
        if (path.endsWith("_block")) {
            path = path.substring(0, path.length() - 6);
            found = true;
        };

        if (found) return new Wood(id.getNamespace(), path.contains("archwood") ? "archwood" : path);

        return null;
    };

    private static final String[] LOG_LOCATIONS = new String[] {
		"x_log", "x_stem", "x_block", // Most cases
		"wood/log/x" // TFC
	};

    public static final Optional<Block> getLogBlockOptional(Wood wood) {
        final String name = wood.name().contains("archwood") ? "blue_archwood" : wood.name();
		for (String location : LOG_LOCATIONS) {
			final Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(wood.namespace(), location.replace("x", name))))
                .map(Holder::value);
			if (block.isPresent()) return block;
		};
		return Optional.empty();
	};

    public static final Block getLogBlockOrOak(Wood wood) {
        return getLogBlockOptional(wood).orElse(Blocks.OAK_LOG);
    };

    @Nullable
    public static final Wood getWoodFromStrippedLog(Object strippedLog) {
        return getWoodFromStrippedLogBlock(BlockHelper.getBlock(strippedLog));
    };

    @Nullable
    public static final Wood getWoodFromStrippedLogBlock(Block strippedLogBlock) {
        if (strippedLogBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(strippedLogBlock);
		String path = id.getPath();

        if (path.contains("stripped_")) {
            path = path.substring(9);

            if (path.endsWith("_log"))
                path = path.substring(0, path.length() - 4);

            if (path.endsWith("_stem"))
                path = path.substring(0, path.length() - 5);
            
            if (path.endsWith("_block"))
                path = path.substring(0, path.length() - 6);

            return new Wood(id.getNamespace(), path.contains("archwood") ? "archwood" : path);
        };

        if (path.contains("wood/stripped_log/")) // TFC
            return new Wood(id.getNamespace(), path.substring(18));

        return null;
    };

    public static final String[] STRIPPED_LOG_LOCATIONS = new String[] {
        "stripped_x_log", "stripped_x_stem", "stripped_x_block", // Most cases
        "wood/stripped_log/x" // TFC
    };

    public static final Optional<Block> getStrippedLogBlockOptional(Wood wood) {
        final String name = wood.name().contains("archwood") ? "blue_archwood" : wood.name();
		for (String location : STRIPPED_LOG_LOCATIONS) {
			final Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(wood.namespace(), location.replace("x", name))))
                .map(Holder::value);
			if (block.isPresent()) return block;
		};
		return Optional.empty();
	};

    public static final Block getStrippedLogBlockOrOak(Wood wood) {
        return getStrippedLogBlockOptional(wood).orElse(Blocks.STRIPPED_OAK_LOG);
    };

    public static record Wood(String namespace, String name) {

        public static final Codec<Wood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("namespace").forGetter(Wood::namespace),
            Codec.STRING.fieldOf("name").forGetter(Wood::name)
        ).apply(instance, Wood::new));

        public static final StreamCodec<ByteBuf, Wood> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Wood::namespace,
            ByteBufCodecs.STRING_UTF8, Wood::name,
            Wood::new
        );

        @Override
        public final boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Wood wood) return namespace().equals(wood.namespace()) && name().equals(wood.name());
            return false; 
        }
    };

    public static final Optional<Block> getDoorBlock(Wood wood) {
        final Optional<Block> doorBlock = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), wood.name() + "_door")).map(Holder::value);
        if (doorBlock.isPresent()) return doorBlock;
        return BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), "wood/door/"+wood.name())).map(Holder::value);
    };

    public static final Block getDoorBlockOrOak(Wood wood) {
        return getDoorBlock(wood).orElse(Blocks.OAK_DOOR);
    };

    @Nullable
    public static final Wood getWoodFromDoor(Object door) {
        return getWoodFromDoorBlock(BlockHelper.getBlock(door));
    };

    @Nullable
    public static final Wood getWoodFromDoorBlock(Block doorBlock) {
        if (doorBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(doorBlock);
		final String path = id.getPath();

		if (path.endsWith("_door")) // Covers most wood types
			return new Wood(id.getNamespace(), path.substring(0, path.length() - 5));

		if (path.contains("wood/door/")) // TerraFirmaCraft
			return new Wood(id.getNamespace(), path.substring(10));

		return null;
    };

    public static final Optional<Block> getTrapdoorBlock(Wood wood) {
        final Optional<Block> trapdoorBlock = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), wood.name() + "_trapdoor")).map(Holder::value);
        if (trapdoorBlock.isPresent()) return trapdoorBlock;
        return BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), "wood/trapdoor/"+wood.name())).map(Holder::value);
    };

    public static final Block getTrapdoorBlockOrOak(Wood wood) {
        return getTrapdoorBlock(wood).orElse(Blocks.OAK_TRAPDOOR);
    };

    @Nullable
    public static final Wood getWoodFromTrapdoor(Object trapdoor) {
        return getWoodFromTrapdoorBlock(BlockHelper.getBlock(trapdoor));
    };

    @Nullable
    public static final Wood getWoodFromTrapdoorBlock(Block trapdoorBlock) {
        if (trapdoorBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(trapdoorBlock);
		final String path = id.getPath();

		if (path.endsWith("_trapdoor")) // Covers most wood types
			return new Wood(id.getNamespace(), path.substring(0, path.length() - 9));

		if (path.contains("wood/trapdoor/")) // TerraFirmaCraft
			return new Wood(id.getNamespace(), path.substring(14));

		return null;
    };
};
