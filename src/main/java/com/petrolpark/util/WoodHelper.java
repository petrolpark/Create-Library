package com.petrolpark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.Mods;
import com.petrolpark.core.world.item.creativeModeTab.CustomTab.ITabEntry;
import com.petrolpark.registry.PetrolparkDataComponentTypes;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
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
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Largely copied from {@link WaterWheelRenderer}.
 */
public class WoodHelper {

    public static final Wood OAK = new Wood("minecraft", "oak");

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

    public static final Stream<Wood> streamAllWoods() {
        return StreamSupport.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.PLANKS).spliterator(), false)
            .map(Holder::value)
            .map(WoodHelper::getWoodFromPlanksBlock);
    };

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

    /**
     * Some Woods (e.g. Ars Nouveau Archwood, Create: Bistro Lemon) have shared Planks, Slabs etc. but different Logs and Leaves.
     * Maps of Logs, Leaves, etc. {@link Wood#name() names} to Planks Woods.
     * Register your own by adding to this List. You should also register the inverse to {@link WoodHelper#SHARED_PLANKS_WOOD_DEFAULTS}.
     */
    public static final List<Function<ResourceLocation, ResourceLocation>> SHARED_PLANKS_WOOD_GETTERS = new ArrayList<>();

    static {
        SHARED_PLANKS_WOOD_GETTERS.add(rl -> rl.getPath().contains("archwood") ? Mods.ARS_NOUVEAU.asResource("archwood") : rl);
    };

    /**
     * Some Woods (e.g. Ars Nouveau Archwood, Create: Bistro Lemon) have shared Planks, Slabs etc. but different Logs, Leaves, etc.
     * Get the Wood used for the Planks from a potential Wood for the Logs, Leaves, etc.
     * @see WoodHelper#SHARED_PLANKS_WOOD_GETTERS
     */
    public static final Wood getSharedPlanksWood(String namespace, String woodPath) {
        ResourceLocation woodLocation = ResourceLocation.fromNamespaceAndPath(namespace, woodPath);
        for (Function<ResourceLocation, ResourceLocation> sharedPlanksWoodGetter : SHARED_PLANKS_WOOD_GETTERS) {
            woodLocation = sharedPlanksWoodGetter.apply(woodLocation);
        };
        return new Wood(woodLocation.getNamespace(), woodLocation.getPath());
    };

    /**
     * Some Woods (e.g. Ars Nouveau Archwood, Create: Bistro Lemon) have shared Planks, Slabs etc. but different Logs and Leaves.
     * Maps Planks Woods to the default Logs, Leaves etc. Wood that should be used.
     * Register your own by adding to this Map. You should also register the inverse to {@link WoodHelper#SHARED_PLANKS_WOOD_GETTERS}.
     */
    public static final Map<Wood, Wood> SHARED_PLANKS_WOOD_DEFAULTS = new HashMap<>();

    static {
        SHARED_PLANKS_WOOD_DEFAULTS.put(new Wood(Mods.ARS_NOUVEAU.getId(), "archwood"), new Wood(Mods.ARS_NOUVEAU.getId(), "blue_archwood"));
    };
    
    /**
     * Some Woods (e.g. Ars Nouveau Archwood, Create: Bistro Lemon) have shared Planks, Slabs etc. but different Logs and Leaves.
     * Get the default Wood for Logs, Leaves, etc. for the given Planks Wood.
     * @param wood
     */
    public static final Wood getDefaultForSharedPlanksWood(Wood wood) {
        final Wood woodLocation = SHARED_PLANKS_WOOD_DEFAULTS.get(wood);
        return woodLocation == null ? wood : woodLocation;
    };

    @Nullable
    public static final Wood getWoodFromSuffixedBlockInTFCPlanksDirectory(Block block, String suffix) {
        if (block == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		final String path = id.getPath();

		if (path.endsWith("_"+suffix)) // Covers most Wood Types
			return new Wood(id.getNamespace(), path.substring(0, path.length() - 1 - suffix.length()));

		if (path.contains("wood/" + suffix + "/")) // TerraFirmaCraft
			return new Wood(id.getNamespace(), path.substring(12));

		return null;
    };

    @Nullable
    public static final Wood getWoodFromSuffixedBlock(Block block, String suffix) {
        if (block == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		String path = id.getPath();

        if (path.endsWith("_" + suffix)) // Covers most Wood Types
            return getSharedPlanksWood(id.getNamespace(), path.substring(0, path.length() - 1 - suffix.length()));

        if (path.contains("wood/" + suffix + "/")) // TFC
            return getSharedPlanksWood(id.getNamespace(), path.substring(6 + suffix.length()));
        
        return null;
    };

    @Nullable
    public static final Wood getWoodFromSuffixedItem(Item item, String suffix) {
        if (item == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(item);
		String path = id.getPath();

        if (path.endsWith("_" + suffix)) // Covers most Wood Types
            return getSharedPlanksWood(id.getNamespace(), path.substring(0, path.length() - 1 - suffix.length()));

        if (path.contains("wood/" + suffix + "/")) // TFC
            return getSharedPlanksWood(id.getNamespace(), path.substring(6 + suffix.length()));
        
        return null;
    };

    public static final Optional<Block> getSuffixedBlockFromWood(Wood wood, String suffix) {
        Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), wood.name() + "_"+ suffix)).map(Holder::value);
        if (block.isPresent()) return block;
        block = BuiltInRegistries.BLOCK.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), "wood/" + suffix + "/" + wood.name())).map(Holder::value);
        if (block.isPresent()) return block;
        final Wood trueWood = getDefaultForSharedPlanksWood(wood);
        return trueWood == wood ? Optional.empty() : getSuffixedBlockFromWood(trueWood, suffix);
    };

    public static final Optional<Block> getBlockFromWoodAndPossibleLocations(Wood wood, String[] possibleLocations) {
        for (String location : possibleLocations) {
			final Optional<Block> block = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(wood.namespace(), location.replace("x", wood.name()))))
                .map(Holder::value);
			if (block.isPresent()) return block;
		};
		final Wood trueWood = getDefaultForSharedPlanksWood(wood);
        return trueWood == wood ? Optional.empty() : getLogBlockOptional(trueWood);
    };

    public static final Optional<Item> getSuffixedItemFromWood(Wood wood, String suffix) {
        Optional<Item> item = BuiltInRegistries.ITEM.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), wood.name() + "_"+ suffix)).map(Holder::value);
        if (item.isPresent()) return item;
        item = BuiltInRegistries.ITEM.getHolder(ResourceLocation.fromNamespaceAndPath(wood.namespace(), "wood/" + suffix + "/" + wood.name())).map(Holder::value);
        if (item.isPresent()) return item;
        final Wood trueWood = getDefaultForSharedPlanksWood(wood);
        return trueWood == wood ? Optional.empty() : getSuffixedItemFromWood(trueWood, suffix);
    };

    public static final Optional<Block> getPlanksBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "planks");
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
        return getWoodFromSuffixedBlockInTFCPlanksDirectory(planksBlock, "planks");
    };

    @Nullable
    public static final Wood getWoodFromSlab(Object slab) {
        return getWoodFromSlabBlock(BlockHelper.getBlock(slab));
    };

    @Nullable
    public static final Wood getWoodFromSlabBlock(Block slabBlock) {
        return getWoodFromSuffixedBlockInTFCPlanksDirectory(slabBlock, "slab");
    };

    @Nullable
    public static final Wood getWoodFromStairs(Object stairs) {
        return getWoodFromStairsBlock(BlockHelper.getBlock(stairs));
    };

    @Nullable
    public static final Wood getWoodFromStairsBlock(Block stairsBlock) {
        return getWoodFromSuffixedBlockInTFCPlanksDirectory(stairsBlock, "stairs");
    };

    @Nullable
    public static final Wood getWoodFromFence(Object fence) {
        return getWoodFromFenceBlock(BlockHelper.getBlock(fence));
    };

    @Nullable
    public static final Wood getWoodFromFenceBlock(Block fenceBlock) {
        return getWoodFromSuffixedBlock(fenceBlock, "fence");
    };

    @Nullable
    public static final Wood getWoodFromFenceGate(Object fenceGate) {
        return getWoodFromFenceGateBlock(BlockHelper.getBlock(fenceGate));
    };

    @Nullable
    public static final Wood getWoodFromFenceGateBlock(Block fenceGateBlock) {
        return getWoodFromSuffixedBlock(fenceGateBlock, "fence_gate");
    };

    @Nullable
    public static final Wood getWoodFromButton(Object button) {
        return getWoodFromButtonBlock(BlockHelper.getBlock(button));
    };

    @Nullable
    public static final Wood getWoodFromButtonBlock(Block buttonBlock) {
        return getWoodFromSuffixedBlock(buttonBlock, "button");
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

        if (found) return getSharedPlanksWood(id.getNamespace(), path);

        return null;
    };

    private static final String[] LOG_LOCATIONS = new String[] {
		"x_log", "x_stem", "x_block", // Most cases
		"wood/log/x" // TFC
	};

    public static final Optional<Block> getLogBlockOptional(Wood wood) {
		return getBlockFromWoodAndPossibleLocations(wood, LOG_LOCATIONS);
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

            return getSharedPlanksWood(id.getNamespace(), path);
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
        return getBlockFromWoodAndPossibleLocations(wood, STRIPPED_LOG_LOCATIONS);
	};

    public static final Block getStrippedLogBlockOrOak(Wood wood) {
        return getStrippedLogBlockOptional(wood).orElse(Blocks.STRIPPED_OAK_LOG);
    };

    @Nullable
    public static final Wood getWoodFromLeaves(Object leavesBlock) {
        return getWoodFromLeavesBlock(BlockHelper.getBlock(leavesBlock));
    };

    @Nullable
    public static final Wood getWoodFromLeavesBlock(Block leavesBlock) {
        if (leavesBlock == null) return null;
        final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(leavesBlock);
		String path = id.getPath();

        if (path.contains("wood/leaves/"))  // TFC
            return new Wood(id.getNamespace(), path.substring(9));

        boolean found = false;

        if (path.endsWith("_leaves")) {
            path = path.substring(0, path.length() - 4);
            found = true;
        };

        if (path.endsWith("_wart_block")) {
            path = path.substring(0, path.length() - 5);
            found = true;
        };

        if (found) return getSharedPlanksWood(id.getNamespace(), path);

        return null;
    };

    public static final String[] LEAVES_LOCATIONS = new String[] {
        "x_leaves", "x_wart_block", // Most cases
        "wood/leaves/x" // TFC
    };

    public static final Optional<Block> getLeavesBlockOptional(Wood wood) {
        return getBlockFromWoodAndPossibleLocations(wood, LEAVES_LOCATIONS);
	};

    public static final Block getLeavesBlockOrOak(Wood wood) {
        return getLeavesBlockOptional(wood).orElse(Blocks.OAK_LEAVES);
    };

    public static final Optional<Block> getSaplingBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "sapling");
    };

    public static final Block getSaplingBlockOrOak(Wood wood) {
        return getSaplingBlock(wood).orElse(Blocks.OAK_SAPLING);
    };

    @Nullable
    public static final Wood getWoodFromSapling(Object sapling) {
        return getWoodFromSaplingBlock(BlockHelper.getBlock(sapling));
    };

    @Nullable
    public static final Wood getWoodFromSaplingBlock(Block saplingBlock) {
        return getWoodFromSuffixedBlock(saplingBlock, "sapling");
    };

    public static final Optional<Block> getPressurePlateBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "pressure_plate");
    };

    public static final Block getPressurePlateBlockOrOak(Wood wood) {
        return getPressurePlateBlock(wood).orElse(Blocks.OAK_PRESSURE_PLATE);
    };

    @Nullable
    public static final Wood getWoodFromPressurePlate(Object pressureplate) {
        return getWoodFromPressurePlateBlock(BlockHelper.getBlock(pressureplate));
    };

    @Nullable
    public static final Wood getWoodFromPressurePlateBlock(Block pressureplateBlock) {
        return getWoodFromSuffixedBlock(pressureplateBlock, "pressure_plate");
    }; 

    public static final Optional<Block> getDoorBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "door");
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
        return getWoodFromSuffixedBlock(doorBlock, "door");
    };

    public static final Optional<Block> getTrapdoorBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "trapdoor");
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
        return getWoodFromSuffixedBlock(trapdoorBlock, "trapdoor");
    };

    public static final Optional<Block> getSignBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "sign");
    };

    public static final Block getSignBlockOrOak(Wood wood) {
        return getSignBlock(wood).orElse(Blocks.OAK_SIGN);
    };

    @Nullable
    public static final Wood getWoodFromSign(Object sign) {
        return getWoodFromSignBlock(BlockHelper.getBlock(sign));
    };

    @Nullable
    public static final Wood getWoodFromSignBlock(Block signBlock) {
        return getWoodFromSuffixedBlock(signBlock, "sign");
    };

    public static final Optional<Block> getHangingSignBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "hanging_sign");
    };

    public static final Block getHangingSignBlockOrOak(Wood wood) {
        return getHangingSignBlock(wood).orElse(Blocks.OAK_HANGING_SIGN);
    };

    @Nullable
    public static final Wood getWoodFromHangingSign(Object hangingSign) {
        return getWoodFromHangingSignBlock(BlockHelper.getBlock(hangingSign));
    };

    @Nullable
    public static final Wood getWoodFromHangingSignBlock(Block hangingSignBlock) {
        return getWoodFromSuffixedBlock(hangingSignBlock, "hanging_sign");
    };

    public static final Optional<Block> getShelfBlock(Wood wood) {
        return getSuffixedBlockFromWood(wood, "shelf");
    };

    // public static final Block getShelfBlockOrOak(Wood wood) {
    //     return getShelfBlock(wood).orElse(Blocks.OAK_SHELF);
    // };

    @Nullable
    public static final Wood getWoodFromShelf(Object shelf) {
        return getWoodFromShelfBlock(BlockHelper.getBlock(shelf));
    };

    @Nullable
    public static final Wood getWoodFromShelfBlock(Block shelfBlock) {
        return getWoodFromSuffixedBlock(shelfBlock, "shelf");
    };

    public static final Optional<Item> getBoatItem(Wood wood) {
        return getSuffixedItemFromWood(wood, "boat");
    };

    public static final Item getBoatItemOrOak(Wood wood) {
        return getBoatItem(wood).orElse(Items.OAK_BOAT);
    };

    @Nullable
    public static final Wood getWoodFromBoatItem(ItemStack boatItem) {
        return getWoodFromSuffixedItem(boatItem.getItem(), "boat");
    };

    public static class WoodenItemTabEntry implements ITabEntry {

        final ItemLike item;

        public WoodenItemTabEntry(ItemLike item) {
            this.item = item;
        };

        @Override
        public void addItems(List<ItemStack> stacks, ItemDisplayParameters parameters, IntConsumer specialRenderLocation) {
            getAll().forEach(stacks::add);
        };

        @Override
        public Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
            return getAll().toList();
        };

        public Stream<ItemStack> getAll() {
            return streamAllWoods().map(wood -> {
                final ItemStack stack = new ItemStack(item);
                stack.set(PetrolparkDataComponentTypes.WOOD, wood);
                return stack;
            });
        };
    };


};
