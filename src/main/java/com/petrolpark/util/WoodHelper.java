package com.petrolpark.util;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.render.StitchedSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Largely copied from {@link WaterWheelRenderer}.
 */
public class WoodHelper {
    
    @OnlyIn(Dist.CLIENT)
    public static final StitchedSprite 
    PLANKS_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_planks")),
	LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log")),
    LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log_top")),
    STRIPPED_LOG_SIDE_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("blocks/stripped_oak_log")),
    STRIPPED_LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/stripped_oak_log_top"));

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
			return new Wood(id.getNamespace(), (path.startsWith("archwood") ? "blue_" : "") + path.substring(0, path.length() - 7));

		if (path.contains("wood/planks/")) // TerraFirmaCraft
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
		final String path = id.getPath();

        if (path.endsWith("_log"))
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 4));

        if (path.endsWith("_stem"))
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 5));
        
        if (path.endsWith("_block"))
            return new Wood(id.getNamespace(), path.substring(0, path.length() - 6));

        if (path.contains("wood/log/")) // TFC
            return new Wood(id.getNamespace(), path.substring(9));

        return null;
    };

    private static final String[] LOG_LOCATIONS = new String[] {
		"x_log", "x_stem", "x_block", // Most cases
		"wood/log/x" // TFC
	};

    public static final Optional<Block> getLogBlockOptional(Wood wood) {
		for (String location : LOG_LOCATIONS) {
			final Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(wood.namespace(), location.replace("x", wood.name()))))
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
                return new Wood(id.getNamespace(), path.substring(0, path.length() - 4));

            if (path.endsWith("_stem"))
                return new Wood(id.getNamespace(), path.substring(0, path.length() - 5));
            
            if (path.endsWith("_block"))
                return new Wood(id.getNamespace(), path.substring(0, path.length() - 6));
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
		for (String location : LOG_LOCATIONS) {
			final Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(wood.namespace(), location.replace("x", wood.name()))))
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
};
