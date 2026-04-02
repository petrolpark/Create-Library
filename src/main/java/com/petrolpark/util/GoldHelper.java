package com.petrolpark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.Mods;
import com.petrolpark.core.recipe.ExampleRecipe;
import com.petrolpark.util.Conversion.ArmorItemStackConversion;
import com.petrolpark.util.Conversion.ConversionResult;
import com.petrolpark.util.Conversion.DyedBlockConversion;
import com.petrolpark.util.Conversion.ItemFrameItemConversion;
import com.petrolpark.util.Conversion.RegisterConversionEvent;
import com.petrolpark.util.Conversion.TieredItemStackConversion;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber
public class GoldHelper {
    
    private static final TreeSet<Conversion.Entry<ItemStack>> GOLD_ITEM_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<BlockAndEntity>> GOLD_BLOCK_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<Entity>> GOLD_ENTITY_CONVERSIONS = new TreeSet<>();

    public static final ConversionResult<ItemStack> convertItemStackToGold(Level level, ItemStack stack, @Nullable Player player) {
        return Conversion.convert(level, stack, player, GOLD_ITEM_CONVERSIONS);
    };

    public static final ConversionResult<BlockAndEntity> convertBlockToGold(Level level, BlockAndEntity block, @Nullable Player player) {
        return Conversion.convert(level, block, player, GOLD_BLOCK_CONVERSIONS);
    };

    public static final ConversionResult<Entity> convertEntityToGold(Level level, Entity entity, @Nullable Player player) {
        return Conversion.convert(level, entity, player, GOLD_ENTITY_CONVERSIONS);
    };

    public static final Stream<RecipeHolder<ExampleRecipe>> streamAllConversions(Collection<ItemStack> ingredients, Level level) {
        final Map<ItemStack, ItemStack> map = ingredients.stream()
            .collect(Collectors.toMap(Function.identity(), stack -> convertItemStackToGold(level, stack, null).object()));
        final Object2ObjectOpenCustomHashMap<ItemStack, List<ItemStack>> recipes = new Object2ObjectOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
        for (Map.Entry<ItemStack, ItemStack> entry : map.entrySet()) {
            if (ItemStack.isSameItemSameComponents(entry.getKey(), entry.getValue())) continue;
            recipes.computeIfAbsent(entry.getValue(), $ -> new ArrayList<>()).add(entry.getKey());
        };
        return recipes.entrySet().stream().map(entry -> new RecipeHolder<>(
            BuiltInRegistries.ITEM.getKey(entry.getKey().getItem()).withPrefix(Petrolpark.MOD_ID + "_gold_conversion_"),
            new ExampleRecipe(Either.left(Ingredient.of(entry.getValue().stream())), Either.left(entry.getKey()))
        ));
    };

    public static class RegisterGoldItemStackConversionEvent extends RegisterConversionEvent<ItemStack> {

        protected RegisterGoldItemStackConversionEvent() {
            super(GOLD_ITEM_CONVERSIONS);
        };

    };

    public static class RegisterGoldBlockConversionEvent extends RegisterConversionEvent<BlockAndEntity> {

        protected RegisterGoldBlockConversionEvent() {
            super(GOLD_BLOCK_CONVERSIONS);
        };

    };

    public static class RegisterGoldEntityConversionEvent extends RegisterConversionEvent<Entity> {

        protected RegisterGoldEntityConversionEvent() {
            super(GOLD_ENTITY_CONVERSIONS);
        };

    };

    @SubscribeEvent
    public static final void onTagsUpdated(TagsUpdatedEvent event) {
        GOLD_ITEM_CONVERSIONS.clear();
        GOLD_BLOCK_CONVERSIONS.clear();
        GOLD_ENTITY_CONVERSIONS.clear();
        NeoForge.EVENT_BUS.post(new RegisterGoldItemStackConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldBlockConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldEntityConversionEvent());
    };

    // DEFAULTS

    public static final DyeColor convertColorToGold(@Nullable Level level, DyeColor color) {
        if (color == null) return DyeColor.YELLOW;
        return switch (color) {
            case ORANGE -> DyeColor.ORANGE;
            case GRAY -> DyeColor.ORANGE;
            case PURPLE -> DyeColor.ORANGE;
            case BROWN -> DyeColor.ORANGE;
            case GREEN -> DyeColor.ORANGE;
            case RED -> DyeColor.ORANGE;
            case BLACK -> DyeColor.ORANGE;
            default -> DyeColor.YELLOW;
        };
    };

    public static final BannerPatternLayers convertBannerToGold(Level level, BannerPatternLayers layers) {
        if (layers == null || layers.layers().isEmpty()) return layers;
        return new BannerPatternLayers(layers.layers().stream().map(layer -> {
            final DyeColor color = convertColorToGold(level, layer.color());
            return color == layer.color() ? layer : new BannerPatternLayers.Layer(layer.pattern(), color);
        }).toList());
    };

    public static final ArmorTrim convertTrimToGold(Level level, ArmorTrim trim) {
        final Holder<TrimMaterial> goldMaterial = level.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL).get(TrimMaterials.GOLD).orElse(null);
        if (goldMaterial == null) return trim;
        if (trim == null || trim.material() == goldMaterial) return trim;
        return new ArmorTrim(goldMaterial, trim.pattern(), trim.showInTooltip);
    };

    public static final class GoldDyedBlockConversion extends DyedBlockConversion {

        public final TagKey<Block> tag;

        public GoldDyedBlockConversion(TagKey<Block> tag) {
            this.tag = tag;
        };

        @Override
        public boolean isValid(BlockAndEntity block) {
            return block.is(tag);
        };

        @Override
        public DyeColor convert(BlockAndEntity block, DyeColor color) {
            return convertColorToGold(null, color);
        };
    };

    @SubscribeEvent
    public static final void onRegisterGoldItemConversions(RegisterGoldItemStackConversionEvent event) {
        register(event, "nothing", Conversion.dontConvert(PetrolparkTags.Items.CANNOT_CONVERT_TO_GOLD::matches), 20000);
        register(event, "contents", Conversion.convertItemStackContainerContents(GoldHelper::convertItemStackToGold), 10000);
        register(event, "trims", Conversion.convertItemComponentIfPresent(DataComponents.TRIM, GoldHelper::convertTrimToGold), 5500);
        register(event, "banner_patterns", Conversion.convertItemComponentIfPresent(DataComponents.BANNER_PATTERNS, GoldHelper::convertBannerToGold), 5000);
        
        register(event, "ingots", Conversion.convertTaggedItem(Tags.Items.INGOTS, Items.GOLD_INGOT), 1000);
        register(event, "nuggets", Conversion.convertTaggedItem(Tags.Items.NUGGETS, Items.GOLD_NUGGET), 1000);
        register(event, "raw_materials", Conversion.convertTaggedItem(Tags.Items.RAW_MATERIALS, Items.RAW_GOLD), 1000);
        register(event, "apples", Conversion.convertTaggedItem(PetrolparkTags.commonItemTag("crops/apple"), Items.GOLDEN_APPLE), 1000); //TODO check tags
        register(event, "carrots", Conversion.convertTaggedItem(Tags.Items.CROPS_CARROT, Items.GOLDEN_CARROT), 1000);
        register(event, "melon_slice", Conversion.convertItem(Items.MELON_SLICE, Items.GLISTERING_MELON_SLICE), 1000);

        register(event, "tiered_items",  // Swords, Shovels, Pickaxes, Axes, Hoes, Spears and most other mod-implemented tiered tools
            new TieredItemStackConversion() {
                @Override
                public Tier convertTier(Level level, ItemStack object, Tier tier) {
                    return Tiers.GOLD;
                };
            },
            1500
        );

        register(event, "armor",
            new ArmorItemStackConversion() {
                @Override
                public Holder<ArmorMaterial> convertArmorMaterial(Level level, ItemStack object, Holder<ArmorMaterial> tier) {
                    return ArmorMaterials.GOLD;
                };
            }, 
        1500
        );

        register(event, "bundles", Conversion.convertItemComponentAndFinish(s -> s.getItem() == Items.BUNDLE, DataComponents.BASE_COLOR, GoldHelper::convertColorToGold), 1000);
        //TODO Ghast Harnesses
        register(event, "block_items", Conversion.convertBlockItem(GoldHelper::convertBlockToGold), 0);
    };

    @SubscribeEvent
    public static final void onRegisterGoldBlockConversions(RegisterGoldBlockConversionEvent event) {
        register(event, "contents", Conversion.convertContainerContents(GoldHelper::convertItemStackToGold), 1000);
        register(event, "metal_blocks", Conversion.convertTaggedBlock(BlockTags.BEACON_BASE_BLOCKS, Blocks.GOLD_BLOCK), 5000);
        register(event, "raw_material_blocks", Conversion.convertBlockIdRegex("^raw_.+_block$", Blocks.RAW_GOLD_BLOCK), 1000);
        registerDyed(event, BlockTags.WOOL);
        registerDyed(event, BlockTags.WOOL_CARPETS);
        register(event, "terracottas", Conversion.convertTaggedBlock(BlockTags.TERRACOTTA, Blocks.YELLOW_TERRACOTTA), 3000); // Don't use both yellow and orange as only yellow looks gold in color
        register(event, "glazed_terracottas", Conversion.convertTaggedBlock(Tags.Blocks.GLAZED_TERRACOTTAS, Blocks.YELLOW_GLAZED_TERRACOTTA), 3000); // Don't use both yellow and orange as only yellow looks gold in color
        registerDyed(event, BlockTags.CONCRETE_POWDER); // Overridden by Gold Dust Block
        registerDyed(event, Tags.Blocks.CONCRETES);
        registerDyed(event, Tags.Blocks.GLASS_BLOCKS);
        registerDyed(event, Tags.Blocks.GLASS_PANES);
        registerDyed(event, BlockTags.BEDS);
        registerDyed(event, BlockTags.BEDS);
        registerDyed(event, BlockTags.CANDLES);
        registerDyed(event, BlockTags.CANDLE_CAKES);
        //TODO Golden Dandelions
        register(event, "polished_blocks", Conversion.convertBlockIdRegex("polished_.+", Blocks.GOLD_BLOCK), 2000); // Don't include polished stairs, walls, etc
        register(event, "other_blocks", Conversion.convertBlockSameClass(Blocks.GOLD_BLOCK), -10000); // Only as a fallback, convert any other simple Blocks into Gold Blocks

        // Compat - Obese Crops
        registerObeseCropsCompat(event, "apple");
        registerObeseCropsCompat(event, "carrot");
        registerObeseCropsCompat(event, "apple_foliage");
        registerObeseCropsCompat(event, "carrot_foliage");
    };

    @SubscribeEvent
    public static final void onRegisterGoldEntityConversions(RegisterGoldEntityConversionEvent event) {
        register(event, "item_frames", new ItemFrameItemConversion(GoldHelper::convertItemStackToGold), 5000);
    
    };

    private static final <T> void register(RegisterConversionEvent<T> event, String name, Conversion<T> conversion, int priority) {
        event.register(Petrolpark.asResource(name), conversion, priority);
    };

    private static final void registerDyed(RegisterGoldBlockConversionEvent event, TagKey<Block> blockTag) {
        register(event, blockTag.location().getPath(), new GoldDyedBlockConversion(blockTag), 300);
    };

    private static final void registerObeseCropsCompat(RegisterGoldBlockConversionEvent event, String name) {
        register(event, "obese_" + name, Conversion.convertBlockIds(Mods.OBESE_CROPS.asResource("obese_" + name), Mods.OBESE_CROPS.asResource("obese_golden_" + name)), 5000);
    };
    
};
