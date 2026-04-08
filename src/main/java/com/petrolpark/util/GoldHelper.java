package com.petrolpark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkMobEffects;
import com.petrolpark.PetrolparkParticleTypes;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.recipe.ExampleRecipe;
import com.petrolpark.util.Conversion.ArmorItemConversion;
import com.petrolpark.util.Conversion.ColorEntityConversion;
import com.petrolpark.util.Conversion.ConversionResult;
import com.petrolpark.util.Conversion.DyedBlockConversion;
import com.petrolpark.util.Conversion.InventoryEntityConversion;
import com.petrolpark.util.Conversion.ItemEntityConversion;
import com.petrolpark.util.Conversion.ItemFrameItemConversion;
import com.petrolpark.util.Conversion.RegisterConversionEvent;
import com.petrolpark.util.Conversion.TieredItemConversion;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class GoldHelper {
    
    private static final TreeSet<Conversion.Entry<Item>> GOLD_ITEM_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<Block>> GOLD_BLOCK_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<ItemStack>> GOLD_ITEM_STACK_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<BlockStateAndEntity>> GOLD_BLOCK_STATE_CONVERSIONS = new TreeSet<>();
    private static final TreeSet<Conversion.Entry<Entity>> GOLD_ENTITY_CONVERSIONS = new TreeSet<>();

    public static final ConversionResult<Item> convertItemToGold(Level level, Item item, @Nullable Player player) {
        return Conversion.convert(level, item, player, GOLD_ITEM_CONVERSIONS);
    };

    public static final ConversionResult<Block> convertBlockToGold(Level level, Block stack, @Nullable Player player) {
        return Conversion.convert(level, stack, player, GOLD_BLOCK_CONVERSIONS);
    };

    public static final ConversionResult<ItemStack> convertItemStackToGold(Level level, ItemStack stack, @Nullable Player player) {
        return Conversion.convert(level, stack, player, GOLD_ITEM_STACK_CONVERSIONS);
    };

    public static final ConversionResult<BlockStateAndEntity> convertBlockStateToGold(Level level, BlockStateAndEntity block, @Nullable Player player) {
        return Conversion.convert(level, block, player, GOLD_BLOCK_STATE_CONVERSIONS);
    };

    public static final ConversionResult<Entity> convertEntityToGold(Level level, Entity entity, @Nullable Player player) {
        return Conversion.convert(level, entity, player, GOLD_ENTITY_CONVERSIONS);
    };

    public static final Stream<RecipeHolder<ExampleRecipe>> streamAllConversions(Collection<ItemStack> ingredients, Level level) {
        final Map<ItemStack, ItemStack> map = ingredients.stream()
            .collect(Collectors.toMap(Function.identity(), stack -> convertItemStackToGold(level, stack, null).value()));
        final Object2ObjectOpenCustomHashMap<ItemStack, List<ItemStack>> recipes = new Object2ObjectOpenCustomHashMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
        for (Map.Entry<ItemStack, ItemStack> entry : map.entrySet()) {
            if (ItemStack.isSameItemSameComponents(entry.getKey(), entry.getValue())) continue;
            recipes.computeIfAbsent(entry.getValue(), $ -> new ArrayList<>()).add(entry.getKey());
        };
        return recipes.entrySet().stream().map(entry -> {
            final ResourceLocation id = BuiltInRegistries.ITEM.getKey(entry.getKey().getItem()); 
            return new RecipeHolder<>(
                Petrolpark.asResource("gold_conversion/" + id.getNamespace() + "/" + id.getPath()),
                new ExampleRecipe(Either.left(Ingredient.of(entry.getValue().stream())), Either.left(entry.getKey()))
            );
        });
    };

    public static class RegisterGoldItemConversionEvent extends RegisterConversionEvent<Item> {

        protected RegisterGoldItemConversionEvent() {
            super(GOLD_ITEM_CONVERSIONS);
        };

    };

    public static class RegisterGoldBlockConversionEvent extends RegisterConversionEvent<Block> {

        protected RegisterGoldBlockConversionEvent() {
            super(GOLD_BLOCK_CONVERSIONS);
        };

    };

    public static class RegisterGoldItemStackConversionEvent extends RegisterConversionEvent<ItemStack> {

        protected RegisterGoldItemStackConversionEvent() {
            super(GOLD_ITEM_STACK_CONVERSIONS);
        };

    };

    public static class RegisterGoldBlockStateConversionEvent extends RegisterConversionEvent<BlockStateAndEntity> {

        protected RegisterGoldBlockStateConversionEvent() {
            super(GOLD_BLOCK_STATE_CONVERSIONS);
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
        GOLD_ITEM_STACK_CONVERSIONS.clear();
        GOLD_BLOCK_STATE_CONVERSIONS.clear();
        GOLD_ENTITY_CONVERSIONS.clear();
        if (!SharedFeatureFlag.GOLD_CONVERSION.enabled()) return;
        NeoForge.EVENT_BUS.post(new RegisterGoldItemConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldBlockConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldItemStackConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldBlockStateConversionEvent());
        NeoForge.EVENT_BUS.post(new RegisterGoldEntityConversionEvent());


    };

    @SubscribeEvent
    public static final void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity && livingEntity.hasEffect(PetrolparkMobEffects.MIDAS_TOUCH.getDelegate()))) return;
        final Level level = livingEntity.level();
        final Player player = livingEntity instanceof Player p ? p : null;
        final AABB box = livingEntity.getBoundingBox().inflate(1 / 16d);
        ItemHelper.modifyItems(livingEntity, stack -> GoldHelper.convertItemStackToGold(level, stack, player).value()); // Convert everything in the Inventory
        level.getEntities(livingEntity, box).forEach(entity -> convertEntityToGold(level, entity, player)); // Convert any touching Entities
        BlockPos.betweenClosedStream(box).forEach(pos -> { // Convert any touching Blocks
            if (level.getBlockState(pos).isAir()) return;
            final BlockStateAndEntity block = BlockStateAndEntity.at(level, pos);
            final BlockStateAndEntity converted = convertBlockStateToGold(level, block, player).value();
            if (!(converted.equals(block))) {
                level.setBlockAndUpdate(pos, converted.state()); 
                converted.entityOp().ifPresent(be -> level.setBlockEntity(be));
                if (level.isClientSide()) ParticleUtils.spawnParticlesOnBlockFaces(level, pos, PetrolparkParticleTypes.GOLD.get(), UniformInt.of(3, 5));
            };
        });
    };

    // DEFAULTS

    public static final Pattern goldIdRegex = Pattern.compile("(^|[_/])(golden|gold)(?=$|[_/])");

    public static final DyeColor convertColorToGold(@Nullable Level level, DyeColor color) {
        if (color == null) return DyeColor.YELLOW;
        return switch (color) {
            // case ORANGE -> DyeColor.ORANGE;
            // case GRAY -> DyeColor.ORANGE;
            // case PURPLE -> DyeColor.ORANGE;
            // case BROWN -> DyeColor.ORANGE;
            // case GREEN -> DyeColor.ORANGE;
            // case RED -> DyeColor.ORANGE;
            // case BLACK -> DyeColor.ORANGE;
            default -> DyeColor.YELLOW; // Just return yellow for now
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

        @Override
        public DyeColor convert(Block block, DyeColor color) {
            return convertColorToGold(null, color);
        };
    };

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static final void onRegisterGoldItemConversions(RegisterGoldItemConversionEvent event) {
        register(event, "impossible", Conversion.dontConvert(item -> goldIdRegex.matcher(item.builtInRegistryHolder().getKey().location().getPath()).find()), 20000);
        register(event, "blocks", Conversion.convertBlockItem(GoldHelper::convertBlockToGold), 0);

        register(event, "ingots", Conversion.convertTaggedItemStrict(Tags.Items.INGOTS, Items.GOLD_INGOT), 1000);
        register(event, "nuggets", Conversion.convertTaggedItemStrict(Tags.Items.NUGGETS, Items.GOLD_NUGGET), 1000);
        register(event, "raw_materials", Conversion.convertTaggedItemStrict(Tags.Items.RAW_MATERIALS, Items.RAW_GOLD), 1000);
        register(event, "apples", Conversion.convertTaggedItemStrict(PetrolparkTags.commonItemTag("crops/apple"), Items.GOLDEN_APPLE), 1000); //TODO check tags
        register(event, "carrots", Conversion.convertTaggedItemStrict(Tags.Items.CROPS_CARROT, Items.GOLDEN_CARROT), 1000);
        register(event, "melon_slice", Conversion.convertItem(Items.MELON_SLICE, Items.GLISTERING_MELON_SLICE), 1000);

        register(event, "tiered_items",  // Swords, Shovels, Pickaxes, Axes, Hoes, Spears and most other mod-implemented tiered tools
            new TieredItemConversion() {
                @Override
                public Tier convertTier(Level level, Item object, Tier tier) {
                    return Tiers.GOLD;
                };
            },
            1500
        );

        register(event, "armor",
            new ArmorItemConversion() {
                @Override
                public Holder<ArmorMaterial> convertArmorMaterial(Level level, Item object, Holder<ArmorMaterial> tier) {
                    return ArmorMaterials.GOLD;
                };
            }, 
            1500
        );
    };

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static final void onRegisterGoldBlockConversions(RegisterGoldBlockConversionEvent event) {
        register(event, "impossible", Conversion.dontConvert(block -> goldIdRegex.matcher(block.builtInRegistryHolder().getKey().location().getPath()).find()), 20000);
        register(event, "pressure_plates", Conversion.convertTaggedBlock(BlockTags.PRESSURE_PLATES, () -> Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), 3000);
        register(event, "metal_blocks", Conversion.convertTaggedBlockStrict(BlockTags.BEACON_BASE_BLOCKS, Blocks.GOLD_BLOCK), 2500);
        register(event, "raw_material_blocks", Conversion.convertBlockIdRegexStrict("^raw_.+_block$", Blocks.RAW_GOLD_BLOCK), 1000);
        //TODO Golden Dandelions
        register(event, "polished_blocks", Conversion.convertBlockIdRegexStrict("polished_.+", Blocks.GOLD_BLOCK), 2000); // Doesn't include polished stairs, walls, etc
        register(event, "other_colored_blocks", new GoldDyedBlockConversion(), 1000);
        register(event, "other_blocks", Conversion.convertBlockSameClass(Blocks.GOLD_BLOCK), -10000); // Only as a fallback, convert any other simple Blocks into Gold Blocks

        if (Mods.OBESE_CROPS.isLoaded()) {
            registerObeseCropsCompat(event, "apple");
            registerObeseCropsCompat(event, "carrot");
            registerObeseCropsCompat(event, "apple_foliage");
            registerObeseCropsCompat(event, "carrot_foliage");
        };

        if (Mods.SUPPLEMENTARIES.isLoaded()) {
            register(event, "supplementaries/iron_gate", Conversion.convertBlockIds(Mods.SUPPLEMENTARIES.asResource("iron_gate"), Mods.SUPPLEMENTARIES.asResource("gold_gate")), 3000);
            register(event, "supplementaries/bars", Conversion.convertBlockIdRegexStrict(".*bars.*", Mods.SUPPLEMENTARIES.block("gold_bars")), 3000);
            register(event, "supplementaries/doors", Conversion.convertTaggedBlock(BlockTags.DOORS, Mods.SUPPLEMENTARIES.asResource("gold_door")), 3000);
            register(event, "supplementaries/trapdoors", Conversion.convertTaggedBlock(BlockTags.TRAPDOORS, Mods.SUPPLEMENTARIES.asResource("gold_trapdoor")), 4000);
        };

        if (Mods.QUARK.isLoaded()) {
            registerQuarkCompat(event, "apple_create");
            registerQuarkCompat(event, "carrot_crate");
            register(event, "quark/buttons", Conversion.convertTaggedBlock(BlockTags.BUTTONS, Mods.QUARK.asResource("gold_button")), 3000);
            register(event, "quark/bars", Conversion.convertBlockIdRegex("^.*bars.*", Mods.QUARK.block("gold_bars")), 3100); // I prefer them to Supplementaries Gold Bars, sorry
            register(event, "quark/bricks", Conversion.convertBlockIdRegex("^.*bricks$", Mods.QUARK.block("raw_gold_bricks")), 2500); // Overriden by Chipped
            register(event, "quark/brick_stairs", Conversion.convertBlockIdRegex("^.+_bricks?_stairs$", Mods.QUARK.block("raw_gold_bricks_stairs")), 3000);
            register(event, "quark/brick_slabs", Conversion.convertBlockIdRegex("^.+_bricks?_slab$", Mods.QUARK.block("raw_gold_bricks_slab")), 3000);
            register(event, "quark/brick_vertical_slabs", Conversion.convertBlockIdRegex("^.+_bricks?_vertical_slab$", Mods.QUARK.block("raw_gold_bricks_vertical_slab")), 3000);
            register(event, "quark/brick_walls", Conversion.convertBlockIdRegex("^.+_bricks?_wall$", Mods.QUARK.block("raw_gold_bricks_wall")), 3000);
            register(event, "quark/other_stairs", Conversion.convertTaggedBlock(BlockTags.STAIRS, Mods.QUARK.block("raw_gold_bricks_stairs")), 1000);
            register(event, "quark/other_slabs", Conversion.convertTaggedBlock(BlockTags.SLABS, Mods.QUARK.block("raw_gold_bricks_slab")), 1000);
            register(event, "quark/other_vertical_slabs", Conversion.convertTaggedBlock(PetrolparkTags.Blocks.VERTICAL_SLABS, Mods.QUARK.block("raw_gold_bricks_vertical_slab")), 1000);
            register(event, "quark/other_walls", Conversion.convertTaggedBlock(BlockTags.WALLS, Mods.QUARK.block("raw_gold_bricks_wall")), 1000);
        };
    };

    @SubscribeEvent
    public static final void onRegisterGoldItemStackConversions(RegisterGoldItemStackConversionEvent event) {
        register(event, "contents", Conversion.convertItemStackContainerContents(GoldHelper::convertItemStackToGold), 10000);
        register(event, "trims", Conversion.convertItemStackComponentIfPresent(DataComponents.TRIM, GoldHelper::convertTrimToGold), 5500);
        register(event, "banner_patterns", Conversion.convertItemStackComponentIfPresent(DataComponents.BANNER_PATTERNS, GoldHelper::convertBannerToGold), 5000);
        register(event, "bundles", Conversion.convertItemStackComponentAndFinish(s -> s.getItem() == Items.BUNDLE, DataComponents.BASE_COLOR, GoldHelper::convertColorToGold), 2000);
        //TODO Ghast Harnesses
        register(event, "block_states", Conversion.convertItemStackBlockState(GoldHelper::convertBlockStateToGold), 1000);
        register(event, "items", Conversion.convertItemStackItem(GoldHelper::convertItemToGold), 0);
    };

    @SubscribeEvent
    public static final void onRegisterGoldBlockStateConversions(RegisterGoldBlockStateConversionEvent event) {
        register(event, "contents", Conversion.convertContainerContents(GoldHelper::convertItemStackToGold), 1000);
        register(event, "blocks", Conversion.convertBlockStateBlock(GoldHelper::convertBlockToGold), 0);
    };

    @SubscribeEvent
    public static final void onRegisterGoldEntityConversions(RegisterGoldEntityConversionEvent event) {
        register(event, "item_entities", new ItemEntityConversion(GoldHelper::convertItemStackToGold), 5000);
        register(event, "item_frames", new ItemFrameItemConversion(GoldHelper::convertItemStackToGold), 5000);
        register(event, "inventories", new InventoryEntityConversion(GoldHelper::convertItemStackToGold), 5000);
        
        register(event, "colored_entities",
            new ColorEntityConversion() {
                @Override
                public DyeColor convert(Entity entity, DyeColor color) {
                    return GoldHelper.convertColorToGold(entity.level(), color);
                };
            },
            2000
        );
    };

    private static final <T> void register(RegisterConversionEvent<T> event, String name, Conversion<T> conversion, int priority) {
        event.register(Petrolpark.asResource(name), conversion, priority);
    };

    private static final void registerObeseCropsCompat(RegisterGoldBlockConversionEvent event, String name) {
        register(event, "obese_crops/" + name, Conversion.convertBlockIds(Mods.OBESE_CROPS.asResource("obese_" + name), Mods.OBESE_CROPS.asResource("obese_golden_" + name)), 5000);
    };

    private static final void registerQuarkCompat(RegisterGoldBlockConversionEvent event, String name) {
        register(event, "quark/" + name, Conversion.convertBlockIds(Mods.QUARK.asResource(name), Mods.QUARK.asResource("golden_" + name)), 5000);
    };
    
};
