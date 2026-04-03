package com.petrolpark.compat.chipped;

import static com.petrolpark.compat.Mods.CHIPPED;

import java.util.function.Supplier;

import com.petrolpark.Petrolpark;
import com.petrolpark.util.BlockHelper;
import com.petrolpark.util.Conversion;
import com.petrolpark.util.GoldHelper.RegisterGoldBlockConversionEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class ChippedConversions {
  
    @SubscribeEvent
    public static final void onRegisterGoldBlockConversions(RegisterGoldBlockConversionEvent event) {
        if (!CHIPPED.isLoaded()) return;

        // Chipped built-in Blocks - always correct
        vanilla(event, Blocks.MELON, "golden_melon");
        palette(event, "melon");
        suffix(event, "panels", Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER);
        metalsPrefix(event, "ancient");
        prefix(event, "chipped");
        prefix(event, "embossed", Blocks.IRON_BLOCK);
        rawPrefix(event, "engraved");
        metalsPrefix(event, "engraved"); // Higher priority than the raw gold ones
        prefix(event, "layered");
        prefix(event, "plated");
        prefix(event, "pressed", Blocks.EMERALD_BLOCK);
        metalsPrefix(event, "reinforced");
        prefix(event, "sheet");
        prefix(event, "shuttered");
        prefix(event, "stacked");
        for (String wood : WoodType.TYPES.keySet()) {
            String leaves = wood + "_leaves";
            vanilla(event, leaves, "golden_" + leaves);
            palette(event, leaves);
            String trapdoor = wood + "_trapdoor";
            vanilla(event, trapdoor, "golden_barred_" + trapdoor); // Lower priority than Supplementaries trapdoor
        };
        vanilla(event, Blocks.PUMPKIN, "goldkin");
        chipped(event, "goldkin", "autumkin", "dewkin", "end_pumpkin", "end_pumpkin_purple", "kabotchkin", "nether_pumpkin", "overgrown_lumpkin", "pimpkin", "rosekin");
        chipped(event, "dirty_goldkin", "dirty_dewkin", "dirty_rosekin", "overgrown_pumpkin", "overgrown_autumkin", "overgrown_pimpkin");
        vanilla(event, Blocks.BARREL, "gold_barrel");
        palette(event, "barrel", "gold_barrel");
        palette(event, "crate", "gold_barrel");
        register(event, "bricks", Conversion.convertBlockIdRegex(CHIPPED.getId(), ".+bricks$", get("raw_gold_block_bricks")), 1000); // Overriden by any other "*_bricks"
        rawSuffix(event, "mini_tiles", Blocks.DARK_PRISMARINE);
        register(event, "bricks", Conversion.convertBlockIdRegex(CHIPPED.getId(), ".+pillar$", get("raw_gold_block_pillar")), 1000); // Overriden by any other "*_pillar"
        rawSuffix(event, "pillar_top");
        rawSuffix(event, "scales");
        rawPrefix(event, "angry");
        rawCircumfix(event, "blank", "carving");
        rawPrefix(event, "carved", Blocks.CHISELED_QUARTZ_BLOCK);
        rawCircumfix(event, "checkered", "tiles", Blocks.PURPUR_BLOCK);
        rawPrefix(event, "cobbled", Blocks.COBBLED_DEEPSLATE);
        rawCircumfix(event, "cracked", "bricks", Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        rawCircumfix(event, "cracked_disordered", "bricks");
        rawCircumfix(event, "cracked_flat", "tiles", Blocks.CRACKED_DEEPSLATE_TILES);
        rawCircumfix(event, "creeper", "carving");
        rawPrefix(event, "crying");
        rawPrefix(event, "cut_blank");
        rawPrefix(event, "glad");
        rawPrefix(event, "duh");
        rawPrefix(event, "eroded", Blocks.STONE, Blocks.CLAY); //TODO Stone stairs, slab, wall
        rawPrefix(event, "etched");
        rawCircumfix(event, "flat", "tiles", Blocks.DEEPSLATE_TILES); //TODO deepslate tile stairs, slab, wall
        rawPrefix(event, "inlayed");
        rawPrefix(event, "inscribed");
        rawCircumfix(event, "layed", "bricks", Blocks.BRICKS); // TODO brick stairs etc
        rawPrefix(event, "loded", Blocks.LODESTONE);
        rawCircumfix(event, "offset", "bricks");
        rawCircumfix(event, "pillar", "bricks", Blocks.QUARTZ_BRICKS);
        rawCircumfix(event, "pillar", "bricks");
        rawCircumfix(event, "prismal", "remnants", Blocks.PRISMARINE_BRICKS); //TODO prismarine slabs, stairs, etc
        rawPrefix(event, "rough", Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE); //TODO blackstone stairs etc
        rawCircumfix(event, "rounded", "bricks");
        rawPrefix(event, "runic_carved");
        rawPrefix(event, "sad");
        rawPrefix(event, "sanded");
        rawPrefix(event, "smooth_inlayed");
        rawPrefix(event, "smooth_ringed");
        rawPrefix(event, "smoothed_double_inlayed");
        rawCircumfix(event, "spider", "carving");
        rawPrefix(event, "spiraled");
        rawCircumfix(event, "stacked", "bricks");
        rawPrefix(event, "tiled"); // Overridden by Tiled * Column and Tiled Bordered *
        rawCircumfix(event, "tiny", "bricks"); // Overriden by Tiny Layered * Bricks
        rawCircumfix(event, "tiny_layered", "bricks");
        rawCircumfix(event, "tiny_layered", "slabs");
        rawPrefix(event, "trodden");
        rawPrefix(event, "unamused");
        rawPrefix(event, "vertical_cut");
        rawCircumfix(event, "vertical_disordered", "bricks");
        rawCircumfix(event, "weathered", "bricks", Blocks.POLISHED_BLACKSTONE); //TODO polished blackstone stairs etc
        rawPrefix(event, "bordered");
        rawPrefix(event, "brick_bordered");
        rawCircumfix(event, "cut", "column");
        rawCircumfix(event, "edged", "bricks");
        rawCircumfix(event, "overlapping", "tiles");
        rawPrefix(event, "polished", Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE); //TODO slabs and stairs for these
        rawCircumfix(event, "smooth", "column");
        rawPrefix(event, "thick_inlayed");
        rawCircumfix(event, "tiled", "column");
        rawPrefix(event, "tiled_bordered");
        rawPrefix(event, "tiny_brick_bordered");
        rawCircumfix(event, "curly", "pillar");
        rawCircumfix(event, "fine", "pillar");
        rawCircumfix(event, "ornate", "pillar");
        rawCircumfix(event, "simple", "pillar");
        rawCircumfix(event, "massive", "bricks");

        // Guesses for other mods
    };

    private static int priorityOffset = 0;

    private static final String[] metalBlockNames = new String[]{"diamond_block", "emerald_block", "iron_block", "netherite_block", "waxed_copper_block", "waxed_exposed_copper_block", "waxed_weather_copper_block", "waxed_oxidized_copper_block"};

    private static final void register(RegisterGoldBlockConversionEvent event, String name, Conversion<Block> conversion, int priority) {
        event.register(Petrolpark.asResource("chipped/" + name), conversion, priority + (priorityOffset++));
    };

    private static final void vanilla(RegisterGoldBlockConversionEvent event, Block block, String name) {
        register(event, name, Conversion.convertBlock(() -> block, BlockHelper.supplier(CHIPPED.asResource(name))), 3000);
    };

    private static final void vanilla(RegisterGoldBlockConversionEvent event, String vanillaName, String name) {
        vanilla(event, BlockHelper.supplier(ResourceLocation.withDefaultNamespace(vanillaName)).get(), name);
    };

    private static final void metalsPrefix(RegisterGoldBlockConversionEvent event, String prefix) {
        for (String name : metalBlockNames) register(event, name, Conversion.convertBlock(get(prefix + "_" + name), get(prefix + "_gold_block")), 3000);
    };

    private static final void chipped(RegisterGoldBlockConversionEvent event, String goldName, String ... otherNames) {
        for (String name : otherNames) register(event, name, Conversion.convertBlock(get(name), get(goldName)), 3000);
    };

    private static final void palette(RegisterGoldBlockConversionEvent event, String palette, String goldName) {
        register(event, palette + "_whole_palette", Conversion.convertBlockIdRegex(CHIPPED.getId(), ".+" + palette, get("golden_" + palette)), 2500);
    };

    private static final void palette(RegisterGoldBlockConversionEvent event, String palette) {
        palette(event, palette, "golden_" + palette);
    };

    private static final void regex(RegisterGoldBlockConversionEvent event, String name, String regex, String goldName, Block ... vanillaBlocks) {
        final Supplier<Block> gold = get(goldName);
        register(event, name, Conversion.convertBlockIdRegex(CHIPPED.getId(), regex, gold), 2500);
        for (Block block : vanillaBlocks) {
            register(event, block.getDescriptionId().split("\\.")[2], Conversion.convertBlock(() -> block, gold), 3000);
        };
    };

    private static final void prefix(RegisterGoldBlockConversionEvent event, String prefix, String regex, Block ... vanillaBlocks) {
        regex(event, prefix + "_blocks", regex, prefix + "_gold_block", vanillaBlocks);
    };

    private static final void prefix(RegisterGoldBlockConversionEvent event, String prefix, Block ... vanillaBlocks) {
        prefix(event, prefix, "^" + prefix + "_.+$", vanillaBlocks);
    };

    private static final void rawPrefix(RegisterGoldBlockConversionEvent event, String prefix, String regex, Block ... vanillaBlocks) {
        regex(event, prefix + "_blocks", regex, prefix + "_raw_gold_block", vanillaBlocks);
    };

    private static final void rawPrefix(RegisterGoldBlockConversionEvent event, String prefix, Block ... vanillaBlocks) {
        rawPrefix(event, prefix, "^" + prefix + "_.+$", vanillaBlocks);
    };

    private static final void suffix(RegisterGoldBlockConversionEvent event, String suffix, Block ... vanillaBlocks) {
        regex(event, "block_" + suffix, "^(.+?)_" + suffix + "$", "gold_block_" + suffix, vanillaBlocks);
    };

    private static final void rawSuffix(RegisterGoldBlockConversionEvent event, String suffix, Block ... vanillaBlocks) {
        regex(event, "block_" + suffix, "^(.+?)_" + suffix + "$", "raw_gold_block_" + suffix, vanillaBlocks);
    };

    private static final void rawCircumfix(RegisterGoldBlockConversionEvent event, String prefix, String suffix, Block ... vanillaBlocks) {
        regex(event, prefix + "_block_" + suffix, "^" + prefix + "_(.+)_" + suffix + "$", prefix + "_raw_gold_block_" + suffix, vanillaBlocks);
    };

    private static final Supplier<Block> get(String name) {
        return BlockHelper.supplier(CHIPPED.asResource(name));
    };
};
