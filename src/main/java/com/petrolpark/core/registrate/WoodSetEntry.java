package com.petrolpark.core.registrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

import com.petrolpark.client.creativemodetab.CustomTab.ITabEntry;
import com.petrolpark.core.registrate.builder.PetrolparkBlockBuilder;
import com.petrolpark.core.world.block.LogBlock;
import com.petrolpark.util.BlockStateProviderHelper;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

public record WoodSetEntry(
    WoodType woodType,
    TagKey<Block> logsBlockTag, TagKey<Item> logsItemTag,
    MapColor planksMapColor, MapColor logMapColor,
    TreeGrower treeGrower,
    Boat.Type boatType,
    BlockEntry<Block> planks,
    BlockEntry<LogBlock> log, BlockEntry<LogBlock> wood, BlockEntry<RotatedPillarBlock> strippedLog, BlockEntry<RotatedPillarBlock> strippedWood,
    BlockEntry<SaplingBlock> sapling, BlockEntry<FlowerPotBlock> pottedSapling, BlockEntry<LeavesBlock> leaves,
    BlockEntry<SlabBlock> slab, BlockEntry<StairBlock> stairs, BlockEntry<FenceBlock> fence, BlockEntry<FenceGateBlock> fenceGate,
    BlockEntry<PressurePlateBlock> pressurePlate, BlockEntry<ButtonBlock> button, BlockEntry<DoorBlock> door, BlockEntry<TrapDoorBlock> trapdoor,
    BlockEntry<StandingSignBlock> standingSign, BlockEntry<WallSignBlock> wallSign, ItemEntry<SignItem> signItem, BlockEntry<CeilingHangingSignBlock> ceilingHangingSign, BlockEntry<WallHangingSignBlock> wallHangingSign, ItemEntry<HangingSignItem> hangingSignItem,
    //TODO shelf
    ItemEntry<BoatItem> boat, ItemEntry<BoatItem> chestBoat,
    List<ItemProviderEntry<?, ?>> additionalEntries
) implements ITabEntry {
    
    public static class Builder<REGISTRATE extends AbstractRegistrate<?>> {

        protected final REGISTRATE registrate;
        protected final WoodType woodType;
        protected final TreeGrower treeGrower;
        protected final Boat.Type boatType;
        protected final String woodName;

        protected String englishName;
        protected NonNullFunction<BlockBehaviour.Properties, LeavesBlock> leavesFactory = LeavesBlock::new;
        protected MapColor planksMapColor = MapColor.WOOD;
        protected MapColor logMapColor = MapColor.PODZOL;
        protected boolean wooden = true;
        protected boolean flammable = true;
        protected boolean randomizePlanksFlip = false;
        protected boolean randomizeLogRotation = false;

        protected List<NonNullBiConsumer<REGISTRATE, WoodSetEntry>> registerCallbacks = new ArrayList<>();

        public Builder(
            REGISTRATE registrate,
            WoodType woodType,
            TreeGrower treeGrower,
            Boat.Type boatType
        ) {
            this.registrate = registrate;
            this.woodType = woodType;
            this.treeGrower = treeGrower;
            this.boatType = boatType;
            this.woodName = ResourceLocation.parse(woodType.name()).getPath();

            englishName = Character.toUpperCase(woodName.charAt(0)) + woodName.substring(1).replaceAll("([a-z])([A-Z])", "$1 $2");
        };

        public WoodSetEntry.Builder<REGISTRATE> lang(String englishName) {
            this.englishName = englishName;
            return this;
        };

        public WoodSetEntry.Builder<REGISTRATE> leavesFactory(NonNullFunction<BlockBehaviour.Properties, LeavesBlock> leavesFactory) {
            this.leavesFactory = leavesFactory;
            return this;
        };

        public WoodSetEntry.Builder<REGISTRATE> mapColors(MapColor planksMapColor, MapColor logMapColor) {
            this.planksMapColor = planksMapColor;
            this.logMapColor = logMapColor;
            return this;
        };

        /**
         * Whether to group blocks like Trapdoors and Fences with the other wooden equivalents, or keep them in separate tags and crafting groups
         */
        public WoodSetEntry.Builder<REGISTRATE> wooden(boolean wooden) {
            this.wooden = wooden;
            return this;
        };

        public WoodSetEntry.Builder<REGISTRATE> flammable(boolean flammable) {
            this.flammable = flammable;
            return this;
        };

        public WoodSetEntry.Builder<REGISTRATE> randomizeLogRotation(boolean randomizeLogRotation) {
            this.randomizeLogRotation = randomizeLogRotation;
            return this;
        };

        public WoodSetEntry.Builder<REGISTRATE> randomizePlanksFlip(boolean randomizePlanksFlip) {
            this.randomizePlanksFlip = randomizePlanksFlip;
            return this;
        };


        public WoodSetEntry.Builder<REGISTRATE> onRegister(NonNullBiConsumer<REGISTRATE, WoodSetEntry> callback) {
            registerCallbacks.add(callback);
            return this;
        };

        public WoodSetEntry register() {

            final ResourceLocation logsTagId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_logs");
            final TagKey<Block> logsBlockTag = TagKey.create(Registries.BLOCK, logsTagId);
            final TagKey<Item> logsItemTag = TagKey.create(Registries.ITEM, logsTagId);

            registrate
                .addDataGenerator(ProviderType.LANG, prov -> {
                    final String name = englishName + " Logs";
                    prov.addTag(() -> logsBlockTag, name);
                    prov.addTag(() -> logsItemTag, name);
                });

            if (wooden) registrate
                .addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.addTag(flammable ? BlockTags.LOGS_THAT_BURN : BlockTags.LOGS).addTag(logsBlockTag))
                .addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.addTag(flammable ? ItemTags.LOGS_THAT_BURN : ItemTags.LOGS).addTag(logsItemTag));

            final ResourceLocation logId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_log");
            final ResourceLocation logTextureId = logId.withPrefix("block/");
            final ResourceLocation strippedLogId = logId.withPrefix("stripped_");
            final ResourceLocation strippedLogTextureId = strippedLogId.withPrefix("block/");
            final ResourceLocation planksId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_planks");
            final ResourceLocation planksTextureId = planksId.withPrefix("block/");

            final FireBlock fire = (FireBlock)Blocks.FIRE;

            final BlockEntry<RotatedPillarBlock> strippedLog = registrate.block(strippedLogId.getPath(), RotatedPillarBlock::new)
                .lang("Stripped " + englishName + " Log")
                .defaultLoot()
                .blockstate((ctx, prov) -> {
                    if (randomizeLogRotation) {
                        BlockStateProviderHelper.randomizedRotationLogBlock(prov, ctx.get());
                    } else {
                        prov.logBlock(ctx.get());
                    };
                }).initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(Tags.Blocks.STRIPPED_LOGS, logsBlockTag)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 5); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(Tags.Items.STRIPPED_LOGS, logsItemTag)
                .build()
                .register();

            final BlockEntry<RotatedPillarBlock> strippedWood = registrate.block("stripped_" + woodName + "_wood", RotatedPillarBlock::new)
                .lang("Stripped " + englishName + " Wood")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.axisBlock(ctx.get(), strippedLogTextureId, strippedLogTextureId))
                .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(Tags.Blocks.STRIPPED_WOODS, logsBlockTag)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.woodFromLogs(prov, ctx.get(), strippedLog))
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 5); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(Tags.Items.STRIPPED_WOODS, logsItemTag)
                .build()
                .register();

            final BlockEntry<LogBlock> log = registrate.block(logId.getPath(), p -> new LogBlock(p, strippedLog::getDefaultState))
                .lang(englishName + " Log")
                .defaultLoot()
                .blockstate((ctx, prov) -> {
                    if (randomizeLogRotation) {
                        BlockStateProviderHelper.randomizedRotationLogBlock(prov, ctx.get());
                    } else {
                        prov.logBlock(ctx.get());
                    };
                }).initialProperties(() -> Blocks.OAK_LOG)
                .properties(p -> p
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? planksMapColor : logMapColor)
                ).tag(logsBlockTag)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 5); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(logsItemTag)
                .build()
                .register();

            final BlockEntry<LogBlock> wood = registrate.block(woodName + "_wood", p -> new LogBlock(p, strippedWood::getDefaultState))
                .initialProperties(() -> Blocks.OAK_WOOD)
                .lang(englishName + " Wood")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.axisBlock(ctx.get(), logTextureId, logTextureId))
                .properties(p -> p
                    .mapColor(logMapColor)
                ).tag(logsBlockTag)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 5); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(logsItemTag)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.woodFromLogs(prov, ctx.get(), log))
                .build()
                .register();

            final BlockEntry<Block> planks = registrate.block(planksId.getPath(), Block::new)
                .initialProperties(() -> Blocks.OAK_PLANKS)
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).lang(englishName + " Planks")
                .defaultLoot()
                .blockstate((ctx, prov) -> {
                    if (randomizePlanksFlip) {
                        prov.simpleBlock(ctx.getEntry(),
                            new ConfiguredModel(prov.cubeAll(ctx.get())),
                            new ConfiguredModel(prov.models()
                                .withExistingParent(ctx.getId().withSuffix("_mirrored").toString(), ResourceLocation.withDefaultNamespace("block/cube_mirrored_all"))
                                .texture("all", planksTextureId)
                            )
                        );
                    } else {
                        prov.simpleBlock(ctx.getEntry());
                    };
                }).transform(b -> wooden ? b.tag(BlockTags.PLANKS) : b)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 20); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .transform(b -> wooden ? b.tag(ItemTags.PLANKS) : b)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.planksFromLogs(prov, ctx.get(), logsItemTag, 4))
                .build()
                .register();

            final ResourceLocation saplingLoc = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_sapling");
            final ResourceLocation saplingTextureLoc = saplingLoc.withPrefix("block/");
            
            final BlockEntry<SaplingBlock> sapling = registrate.block(saplingLoc.getPath(), p -> new SaplingBlock(treeGrower, p))
                .initialProperties(() -> Blocks.OAK_SAPLING)
                .lang(englishName + " Sapling")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cross(saplingLoc.getPath(), saplingTextureLoc).renderType("cutout")))
                .tag(BlockTags.SAPLINGS)
                .item()
                .model((ctx, prov) -> prov.generated(ctx, saplingTextureLoc))
                .tag(ItemTags.SAPLINGS)
                .build()
                .register();

            final ResourceLocation pottedSaplingId = saplingLoc.withPrefix("potted_");

            final BlockEntry<FlowerPotBlock> pottedSapling = registrate.block(pottedSaplingId.getPath(), p -> new FlowerPotBlock(() -> (FlowerPotBlock)Blocks.FLOWER_POT, sapling::get, p))
                .initialProperties(() -> Blocks.POTTED_OAK_SAPLING)
                .lang("Potted " + englishName + " Sapling")
                .loot((lt, b) -> lt.add(b, lt.createPotFlowerItemTable(sapling)))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().singleTexture(pottedSaplingId.withPrefix("block/").getPath(), ResourceLocation.withDefaultNamespace("block/flower_pot_cross"), "plant", saplingTextureLoc)))
                .tag(BlockTags.FLOWER_POTS)
                .register();

            final BlockEntry<LeavesBlock> leaves = registrate.block(woodName + "_leaves", leavesFactory)
                .initialProperties(()  -> Blocks.OAK_LEAVES)
                .lang(englishName + " Leaves")
                .loot((lt, b) -> lt.add(b, lt.createLeavesDrops(b, sapling.get(), new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f})))
                .defaultBlockstate()
                .tag(BlockTags.LEAVES)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 30, 60); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(ItemTags.LEAVES)
                .compostable(0.3f)
                .build()
                .register();

            final BlockEntry<SlabBlock> slab = registrate.block(woodName + "_slab", SlabBlock::new)
                .initialProperties(() -> Blocks.OAK_SLAB)
                .lang(englishName + " Slab")
                .loot((lt, b) -> lt.add(b, lt.createSlabItemTable(b)))
                .blockstate((ctx, prov) -> prov.slabBlock(ctx.get(), planksId, planksTextureId))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_SLABS : BlockTags.SLABS)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 20); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(wooden ? ItemTags.WOODEN_SLABS : ItemTags.SLABS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_slab" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final BlockEntry<StairBlock> stairs = registrate.block(woodName + "_stairs", p -> new StairBlock(planks.getDefaultState(), p))
                .initialProperties(() -> Blocks.OAK_STAIRS)
                .lang(englishName + " Stairs")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.stairsBlock(ctx.get(), planksTextureId))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_STAIRS : BlockTags.STAIRS)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 20); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(wooden ? ItemTags.WOODEN_STAIRS : ItemTags.STAIRS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.stairBuilder(ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_stairs" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final BlockEntry<FenceBlock> fence = registrate.block(woodName + "_fence", FenceBlock::new)
                .initialProperties(() -> Blocks.OAK_FENCE)
                .lang(englishName + " Fence")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.fenceBlock(ctx.get(), planksTextureId))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_FENCES : BlockTags.FENCES)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 20); })
                .item()
                .model((ctx, prov) -> prov.fenceInventory(woodName + "_fence", planksTextureId))
                .tag(wooden ? ItemTags.WOODEN_FENCES : ItemTags.FENCES)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.fenceBuilder(ctx.get(), Ingredient.of(planks))
                    .group("fence")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final BlockEntry<FenceGateBlock> fenceGate = registrate.block(woodName + "_fence_gate", p -> new FenceGateBlock(woodType, p))
                .initialProperties(() -> Blocks.OAK_FENCE_GATE)
                .lang(englishName + " Fence Gate")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.fenceGateBlock(ctx.get(), planksTextureId))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? Tags.Blocks.FENCE_GATES_WOODEN : BlockTags.FENCE_GATES)
                .onRegister(block -> { if (flammable) fire.setFlammable(block, 5, 20); })
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(wooden ? Tags.Items.FENCE_GATES_WOODEN : ItemTags.FENCE_GATES)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.fenceGateBuilder(ctx.get(), Ingredient.of(planks))
                    .group("fence_gate")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final BlockEntry<PressurePlateBlock> pressurePlate = registrate.block(woodName + "_pressure_plate", p -> new PressurePlateBlock(woodType.setType(), p))
                .initialProperties(() -> Blocks.OAK_PRESSURE_PLATE)
                .lang(englishName + " Pressure Plate")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.pressurePlateBlock(ctx.get(), planksTextureId))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_PRESSURE_PLATES : BlockTags.PRESSURE_PLATES)
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(wooden ? ItemTags.WOODEN_PRESSURE_PLATES : ItemTags.WOODEN_PRESSURE_PLATES) //TODO non-wooden pressure plate tag
                .recipe((ctx, prov) -> RegistrateRecipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_pressure_plate" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final BlockEntry<ButtonBlock> button = registrate.block(woodName + "_button", p -> new ButtonBlock(woodType.setType(), 30, p))
                .initialProperties(() -> Blocks.OAK_BUTTON)
                .lang(englishName + " Button")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.buttonBlock(ctx.get(), planksTextureId))
                .tag(wooden ? BlockTags.WOODEN_BUTTONS : BlockTags.BUTTONS)
                .transform(PetrolparkBlockBuilder::defaultBlockItem)
                .tag(wooden ? ItemTags.WOODEN_BUTTONS : ItemTags.BUTTONS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.buttonBuilder(ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_button" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final ResourceLocation doorId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_door");
            final ResourceLocation doorTextureId = doorId.withPrefix("block/");

            final BlockEntry<DoorBlock> door = registrate.block(doorId.getPath(), p -> new DoorBlock(woodType.setType(), p))
                .initialProperties(() -> Blocks.OAK_DOOR)
                .lang(englishName + " Door")
                .loot((lt, b) -> lt.add(b, lt.createDoorTable(b)))
                .blockstate((ctx, prov) -> prov.doorBlockWithRenderType(ctx.get(), woodName, doorTextureId.withSuffix("_bottom"), doorTextureId.withSuffix("_top"), "cutout"))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_DOORS : BlockTags.DOORS)
                .item()
                .defaultModel()
                .tag(wooden ? ItemTags.WOODEN_DOORS : ItemTags.DOORS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.doorBuilder(ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_door" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();

            final ResourceLocation trapdoorId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_trapdoor");

            final BlockEntry<TrapDoorBlock> trapdoor = registrate.block(trapdoorId.getPath(), p -> new TrapDoorBlock(woodType.setType(), p))
                .initialProperties(() -> Blocks.OAK_TRAPDOOR)
                .lang(englishName + " Trapdoor")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.trapdoorBlockWithRenderType(ctx.get(), woodName, trapdoorId.withPrefix("block/"), true, "cutout"))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(wooden ? BlockTags.WOODEN_TRAPDOORS : BlockTags.TRAPDOORS)
                .item()
                .model((ctx, prov) -> prov.blockItem(ctx::get, "_bottom"))
                .tag(wooden ? ItemTags.WOODEN_TRAPDOORS : ItemTags.TRAPDOORS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.trapdoorBuilder(ctx.get(), Ingredient.of(planks))
                    .group(wooden ? "wooden_trapdoor" : "")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).build()
                .register();
            
            final ResourceLocation signId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_sign");
            final ResourceLocation signModelId = signId.withPrefix("block/");

            final BlockEntry<StandingSignBlock> standingSign = registrate.block(signId.getPath(), p -> new StandingSignBlock(woodType, p))
                .initialProperties(() -> Blocks.OAK_SIGN)
                .lang(englishName + " Sign")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().sign(signModelId.getPath(), planksTextureId)))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(BlockTags.SIGNS) //TODO non-wooden signs
                .register();

            final ResourceLocation wallSignId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_wall_sign");

            final BlockEntry<WallSignBlock> wallSign = registrate.block(wallSignId.getPath(), p -> new WallSignBlock(woodType, p))
                .initialProperties(() -> Blocks.OAK_WALL_SIGN)
                .lang($ -> Util.makeDescriptionId("block", wallSignId), englishName + " Wall Sign")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(signModelId)))
                .properties(p -> p
                    .mapColor(planksMapColor)
                ).tag(BlockTags.WALL_SIGNS) //TODO non-wooden signs
                .register();

            OneTimeEventReceiver.addModListener(registrate, BlockEntityTypeAddBlocksEvent.class, e -> e.modify(BlockEntityType.SIGN, standingSign.get(), wallSign.get()));

            final ItemEntry<SignItem> signItem = registrate.item(woodName + "_sign", p -> new SignItem(p, standingSign.get(), wallSign.get()))
                .defaultModel()
                .tag(ItemTags.SIGNS) //TODO non-wooden signs
                .recipe((ctx, prov) -> RegistrateRecipeProvider.signBuilder(ctx.get(), Ingredient.of(planks))
                    .group("sign")
                    .unlockedBy("has_planks", RegistrateRecipeProvider.has(planks))
                    .save(prov)
                ).register();

            final ResourceLocation hangingSignId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_hanging_sign");
            final ResourceLocation hangingSignModelId = hangingSignId.withPrefix("block/");

            final BlockEntry<CeilingHangingSignBlock> ceilingHangingSign = registrate.block(hangingSignId.getPath(), p -> new CeilingHangingSignBlock(woodType, p))
                .initialProperties(() -> Blocks.OAK_HANGING_SIGN)
                .lang(englishName + " Hanging Sign")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().sign(hangingSignModelId.getPath(), strippedLogTextureId)))
                .properties(p -> p
                    .mapColor(logMapColor)
                ).tag(BlockTags.CEILING_HANGING_SIGNS) //TODO non-wooden signs
                .register();

            final ResourceLocation wallHangingSignId = ResourceLocation.fromNamespaceAndPath(registrate.getModid(), woodName + "_wall_hanging_sign");

            final BlockEntry<WallHangingSignBlock> wallHangingSign = registrate.block(wallHangingSignId.getPath(), p -> new WallHangingSignBlock(woodType, p))
                .initialProperties(() -> Blocks.OAK_WALL_HANGING_SIGN)
                .lang($ -> Util.makeDescriptionId("block", wallHangingSignId), englishName + " Wall Hanging Sign")
                .defaultLoot()
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(hangingSignModelId)))
                .properties(p -> p
                    .mapColor(logMapColor)
                ).tag(BlockTags.WALL_HANGING_SIGNS) //TODO non-wooden signs
                .register();

            OneTimeEventReceiver.addModListener(registrate, BlockEntityTypeAddBlocksEvent.class, e -> e.modify(BlockEntityType.HANGING_SIGN, ceilingHangingSign.get(), wallHangingSign.get()));

            final ItemEntry<HangingSignItem> hangingSignItem = registrate.item(woodName + "_hanging_sign", p -> new HangingSignItem(ceilingHangingSign.get(), wallHangingSign.get(), p))
                .defaultModel()
                .tag(ItemTags.HANGING_SIGNS) //TODO non-wooden signs
                .recipe((ctx, prov) -> RegistrateRecipeProvider.hangingSign(prov, ctx.get(), planks))
                .register();

            final ItemEntry<BoatItem> boat = registrate.item(woodName + "_boat", p -> new BoatItem(false, boatType, p))
                .lang(englishName + " Boat")
                .defaultModel()
                .tag(ItemTags.BOATS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.woodenBoat(prov, ctx.get(), planks))
                .register();
            
            final ItemEntry<BoatItem> chestBoat = registrate.item(woodName + "_chest_boat", p -> new BoatItem(true, boatType, p))
                .lang(englishName + " Boat with Chest")
                .defaultModel()
                .tag(ItemTags.BOATS)
                .recipe((ctx, prov) -> RegistrateRecipeProvider.chestBoat(prov, ctx.get(), boat))
                .register();

            final WoodSetEntry setEntry = new WoodSetEntry(
                woodType,
                logsBlockTag, logsItemTag,
                planksMapColor, logMapColor,
                treeGrower,
                boatType,
                planks,
                log, wood, strippedLog, strippedWood,
                sapling, pottedSapling, leaves,
                slab, stairs, fence, fenceGate,
                pressurePlate, button, door, trapdoor,
                standingSign, wallSign, signItem, ceilingHangingSign, wallHangingSign, hangingSignItem,
                boat, chestBoat,
                new ArrayList<>()
            );

            registerCallbacks.forEach(callback -> callback.accept(registrate, setEntry));

            return setEntry;
        };
    };

    public Stream<ItemProviderEntry<?, ?>> streamItemEntries() {
        return Stream.concat(Stream.of(
            log(), wood(), strippedLog(), strippedWood(), planks(), stairs(), slab(), fence(), fenceGate(), door(), trapdoor(), button(), // Building blocks
            leaves(), sapling(), // Natural blocks
            signItem(), hangingSignItem(), // Functional blocks
            boat(), chestBoat() // Tools & utilities
        ), additionalEntries().stream()); // Any others
    };

    @Override
    public void addItems(List<ItemStack> stacks, ItemDisplayParameters parameters, IntConsumer specialRenderLocation) {
        stacks.addAll(streamItemEntries().map(ItemProviderEntry::asStack).toList());
    };

    @Override
    public Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
        return streamItemEntries().map(ItemProviderEntry::asStack).toList();
    };
};
