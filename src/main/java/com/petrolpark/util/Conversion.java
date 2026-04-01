package com.petrolpark.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;

@FunctionalInterface
public interface Conversion<T> {
    
    public ConversionResult<T> convert(Level level, T object, @Nullable Player player);

    public default ConversionResult.Pass<T> pass(T object) {
        return new ConversionResult.Pass<>(object);
    };

    public default Supplier<ConversionResult<T>> supplyPass(T object) {
        return () -> pass(object);
    };

    public default ConversionResult.Finish<T> finish(T object) {
        return new ConversionResult.Finish<T>(object);
    };

    public default <U, S extends U> boolean isInstance(U object, Class<S> subClass) {
        return object.getClass() == subClass;
    };

    public static <T> ConversionResult<T> convert(Level level, T object, @Nullable Player player, Collection<Conversion.Entry<T>> conversions) {
        for (Conversion.Entry<T> entry : conversions) {
            try {
                final ConversionResult<T> result = entry.conversion().convert(level, object, player);
                object = result.object();
                if (result.finish()) return result;
            } catch (Throwable e) {
                throw new RuntimeException("Problem while running converter " + entry.id().toString(), e);
            };
        };
        return new ConversionResult.Pass<>(object);
    };

    public static <T> NoConversion<T> dontConvert(Predicate<T> predicate) {
        return new NoConversion<>(predicate);
    };

    public static MatchingItemStackConversion convertItem(Item item, Item result) {
        return convertItem(() -> item, () -> result);
    };

    public static MatchingItemStackConversion convertItemIds(ResourceLocation id, ResourceLocation resultId) {
        return convertItem(Suppliers.memoize(() -> BuiltInRegistries.ITEM.get(id)), Suppliers.memoize(() -> BuiltInRegistries.ITEM.get(resultId)));
    };

    public static MatchingItemStackConversion convertItem(Supplier<Item> item, Supplier<Item> result) {
        return new MatchingItemStackConversion(s -> s.getItem() == item.get(), false, result);
    };

    public static MatchingItemStackConversion convertTaggedItem(TagKey<Item> tag, Item item) {
        return convertTaggedItem(tag, () -> item);
    };

    public static MatchingItemStackConversion convertTaggedItem(TagKey<Item> tag, Supplier<Item> item) {
        return new MatchingItemStackConversion(stack -> stack.is(tag), true, item);
    };

    public static MatchingItemStackConversion convertItemIdRegex(String regex, Supplier<Item> item) {
        final Pattern pattern = Pattern.compile(regex);
        return new MatchingItemStackConversion(stack -> pattern.matcher(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()).matches(), true, item);
    };

    public static MatchingItemStackConversion convertItemSameClass(Item item) {
        return convertItemSameClass(() -> item);
    };

    public static MatchingItemStackConversion convertItemSameClass(Supplier<Item> item) {
        return new MatchingItemStackConversion(Predicates.alwaysTrue(), true, item);
    };

    public static MatchingItemStackConversion convertItemMatching(Predicate<ItemStack> predicate, Item item) {
        return new MatchingItemStackConversion(predicate, true, () -> item);  
    };

    public static BlockItemConversion convertBlockItem(Conversion<BlockAndEntity> blockConversion) {
        return new BlockItemConversion(blockConversion);
    };

    public static <T> ItemStackComponentConversion<T> convertItemComponentIfPresent(DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemComponent(s -> s.has(component), component, map);
    };

    public static <T> ItemStackComponentConversion<T> convertItemComponent(Predicate<ItemStack> predicate, DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemComponent(predicate, () -> component, map, true);
    };

    public static <T> ItemStackComponentConversion<T> convertItemComponentAndFinish(Predicate<ItemStack> predicate, DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemComponent(predicate, () -> component, map, false);
    };

    public static <T> ItemStackComponentConversion<T> convertItemComponent(Predicate<ItemStack> predicate, Supplier<DataComponentType<T>> component, BiFunction<Level, T, T> map, boolean pass) {
        return new ItemStackComponentConversion<>(predicate, component, map, pass);
    };

    public static ItemStackContainerConversion convertItemStackContainerContents(Conversion<ItemStack> contentsConversion) {
        return new ItemStackContainerConversion(contentsConversion);
    };

    public static MatchingBlockConversion convertBlock(Block block, Block result) {
        return convertBlock(() -> block, () -> result);
    };

    public static MatchingBlockConversion convertBlockIds(ResourceLocation id, ResourceLocation resultId) {
        return convertBlock(Suppliers.memoize(() -> BuiltInRegistries.BLOCK.get(id)), Suppliers.memoize(() -> BuiltInRegistries.BLOCK.get(resultId)));
    };

    public static MatchingBlockConversion convertBlock(Supplier<Block> block, Supplier<Block> result) {
        return new MatchingBlockConversion(s -> s.block() == block.get(), false, result);
    };

    public static MatchingBlockConversion convertTaggedBlock(TagKey<Block> tag, Block block) {
        return convertTaggedBlock(tag, () -> block);
    };

    public static MatchingBlockConversion convertTaggedBlock(TagKey<Block> tag, Supplier<Block> block) {
        return new MatchingBlockConversion(b -> b.state().is(tag), true, block);
    };

    public static MatchingBlockConversion convertBlockIdRegex(String regex, Block block) {
        return convertBlockIdRegex(regex, () -> block);
    };

    public static MatchingBlockConversion convertBlockIdRegex(String regex, Supplier<Block> block) {
        final Pattern pattern = Pattern.compile(regex);
        return new MatchingBlockConversion(b -> pattern.matcher(BuiltInRegistries.BLOCK.getKey(b.state().getBlock()).getPath()).matches(), true, block);
    };

    public static MatchingBlockConversion convertBlockSameClass(Block block) {
        return convertBlockSameClass(() -> block);
    };

    public static MatchingBlockConversion convertBlockSameClass(Supplier<Block> block) {
        return new MatchingBlockConversion(Predicates.alwaysTrue(), true, block);  
    };

    public static ContainerConversion convertContainerContents(Conversion<ItemStack> contentsConversion) {
        return new ContainerConversion(contentsConversion);
    };

    public record NoConversion<T>(Predicate<T> predicate) implements Conversion<T> {

        @Override
        public ConversionResult<T> convert(Level level, T object, @Nullable Player player) {
            return predicate().test(object) ? finish(object) : pass(object);
        };

    };

    public interface ItemStackConversion extends Conversion<ItemStack> {

        public default ConversionResult<ItemStack> swap(ItemStack stack, Item item) {
            return swap(stack, item, true);
        };

        public default ConversionResult<ItemStack> swap(ItemStack stack, Item item, boolean classesMustMatch) {
            if (!isInstance(stack.getItem(), item.getClass()) && classesMustMatch) return pass(stack);
            final ItemStack result = new ItemStack(item, stack.getCount());
            result.applyComponents(stack.getComponentsPatch());
            return finish(result);
        };
    };

    public record MatchingItemStackConversion(Predicate<ItemStack> predicate, boolean classesMustMatch, Supplier<Item> item) implements ItemStackConversion {

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            final Item item = item().get();
            return item != null && predicate().test(object) ? swap(object, item, classesMustMatch()) : pass(object); 
        };

    };

    public record BlockItemConversion(Conversion<BlockAndEntity> blockConversion) implements ItemStackConversion {

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (object.getItem() instanceof BlockItem blockItem) {
                final Item item = blockConversion().convert(level, new BlockAndEntity(object.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState()), null), player).object().state().getBlock().asItem();
                if (item != Items.AIR) return swap(object, item);
            };
            return pass(object);
        };

    };

    public record ItemStackComponentConversion<T>(Predicate<ItemStack> predicate, Supplier<DataComponentType<T>> component, BiFunction<Level, T, T> map, boolean pass) implements ItemStackConversion {

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (!predicate().test(object)) return pass(object);
            object.set(component().get(), map.apply(level, object.get(component())));
            return pass() ? pass(object) : finish(object);
        };
    };

    public record ItemStackContainerConversion(Conversion<ItemStack> contentsConversion) implements ItemStackConversion {

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (object.has(DataComponents.CONTAINER)) {
                object.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(
                    object.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream()
                        .map(stack -> contentsConversion().convert(level, stack, player).object())
                        .toList()
                ));
            };
            if (object.has(DataComponents.BUNDLE_CONTENTS)) {
                object.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(
                    object.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).itemCopyStream()
                        .map(stack -> contentsConversion().convert(level, stack, player).object())
                        .toList()
                ));
            };
            return pass(object);
        };
    };

    public abstract class TieredItemStackConversion implements ItemStackConversion {

        protected final Map<Class<? extends TieredItem>, Map<Item, Optional<TieredItem>>> map = new HashMap<>();

        public abstract Tier convertTier(Level level, ItemStack object, Tier tier);

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (object.getItem() instanceof TieredItem tieredItem) {
                final Class<? extends TieredItem> tieredItemClass = tieredItem.getClass();
                return map.computeIfAbsent(tieredItemClass, $ -> new HashMap<>())
                    .computeIfAbsent(tieredItem, $ -> {
                        final Tier tier = convertTier(level, object, tieredItem.getTier());
                        return BuiltInRegistries.ITEM.stream()
                            .filter(item -> item.getClass() == tieredItemClass)
                            .map(item -> (TieredItem)item)
                            .filter(item -> item.getTier() == tier)
                            .findFirst();
                    }).map(item -> swap(object, item)).orElse(pass(object));
            };
            return pass(object);
        };

    };

    public abstract class ArmorItemStackConversion implements ItemStackConversion {

        protected final Map<ArmorItem.Type, Map<Item, Optional<ArmorItem>>> map = new HashMap<>();

        public abstract Holder<ArmorMaterial> convertArmorMaterial(Level level, ItemStack object, Holder<ArmorMaterial> tier);

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (object.getItem() instanceof ArmorItem armorItem) {
                return map.computeIfAbsent(armorItem.getType(), $ -> new HashMap<>())
                    .computeIfAbsent(armorItem, $ -> {
                        final Holder<ArmorMaterial> armorMaterial = convertArmorMaterial(level, object, armorItem.getMaterial());
                        return BuiltInRegistries.ITEM.stream()
                            .filter(item -> item instanceof ArmorItem armorItem2 && armorItem2.getType() == armorItem.getType())
                            .map(item -> (ArmorItem)item)
                            .filter(item -> item.getMaterial() == armorMaterial)
                            .findFirst();
                    }).map(item -> swap(object, item)).orElse(pass(object));
            };
            return pass(object);
        };
    };

    public interface BlockConversion extends Conversion<BlockAndEntity> {

        public default ConversionResult<BlockAndEntity> swap(BlockAndEntity blockAndEntity, Block block) {
            return swap(blockAndEntity, block, true);
        };

        public default ConversionResult<BlockAndEntity> swap(BlockAndEntity blockAndEntity, Block block, boolean classesMustMatch) {
            if (!isInstance(blockAndEntity.state().getBlock(), block.getClass()) && classesMustMatch) return pass(blockAndEntity);
            return blockAndEntity.withStateOptional(BlockHelper.copyAll(block.defaultBlockState(), blockAndEntity.state()))
                .<ConversionResult<BlockAndEntity>>map(this::finish)
                .orElseGet(supplyPass(blockAndEntity));
        };
    };

    public record MatchingBlockConversion(Predicate<BlockAndEntity> predicate, boolean classesMustMatch, Supplier<Block> block) implements BlockConversion {

        @Override
        public ConversionResult<BlockAndEntity> convert(Level level, BlockAndEntity object, @Nullable Player player) {
            final Block block = block().get();
            return block != null && predicate().test(object) ? swap(object, block, classesMustMatch()) : pass(object);
        };
        
    };

    public record ContainerConversion(Conversion<ItemStack> itemConversion) implements BlockConversion {

        @Override
        public ConversionResult<BlockAndEntity> convert(Level level, BlockAndEntity object, @Nullable Player player) {
            object.entityOp().ifPresent(be -> {
                if (be instanceof Container container) {
                    for (int slot = 0; slot < container.getContainerSize(); slot++) {
                        final ItemStack stack = container.getItem(slot);
                        final ItemStack converted = itemConversion().convert(level, stack, player).object();
                        if (!ItemStack.isSameItemSameComponents(stack, converted)) container.setItem(slot, converted);
                    };
                };
            });
            return pass(object);
        };

    };

    public static abstract class DyedBlockConversion implements BlockConversion {

        protected final Map<Block, Block> blocks = new HashMap<>();

        public abstract boolean isValid(BlockAndEntity block);

        public abstract DyeColor convert(BlockAndEntity block, DyeColor color);

        @Override
        public ConversionResult<BlockAndEntity> convert(Level level, BlockAndEntity object, @Nullable Player player) {
            if (!isValid(object)) return pass(object);
            final Block block = blocks.computeIfAbsent(object.block(), b -> {
                final Optional<BiMap<DyeColor, Block>> map = DyeHelper.getMap(b);
                if (map.isEmpty()) return null; // Not dyed
                return map.get().get(convert(object, map.get().inverse().get(b)));
            });
            return block == null ? pass(object) : swap(object, block); 
        };

    };

    public record ItemFrameItemConversion(Conversion<ItemStack> itemConversion) implements Conversion<Entity> {

        @Override
        public ConversionResult<Entity> convert(Level level, Entity object, @Nullable Player player) {
            if (object instanceof ItemFrame itemFrame) {
                itemFrame.setItem(itemConversion().convert(level, itemFrame.getItem(), player).object());
                return finish(itemFrame);
            } else {
                return pass(object);
            }
        };

    };

    public sealed interface ConversionResult<T> permits ConversionResult.Pass, ConversionResult.Finish {

        public T object();

        public boolean pass();

        public default boolean finish() {
            return !pass();
        };

        public static record Pass<T>(T object) implements ConversionResult<T> {

            @Override
            public boolean pass() {
                return true;
            };
        };

        public static record Finish<T>(T object) implements ConversionResult<T> {

            @Override
            public boolean pass() {
                return false;
            };
        };
    };

    public record Entry<T>(ResourceLocation id, Conversion<T> conversion, int priority) implements Comparable<Conversion.Entry<T>> {

        @Override
        public int compareTo(Conversion.Entry<T> o) {
            if (priority() == o.priority()) {
                return id().compareTo(o.id());
            } else {
                return o.priority() - priority();
            }
        };

    };

    public static abstract class RegisterConversionEvent<T> extends Event {

        protected final Collection<Conversion.Entry<T>> conversions;

        protected RegisterConversionEvent(Collection<Conversion.Entry<T>> conversions) {
            this.conversions = conversions;
        };

        public void register(ResourceLocation id, Conversion<T> conversion, int priority) {
            conversions.add(new Conversion.Entry<>(id, conversion, priority));
        };
    };
};
