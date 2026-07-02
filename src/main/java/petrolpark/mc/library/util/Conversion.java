package petrolpark.mc.library.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
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
import petrolpark.mc.library.shared.world.GoldConversion;

/**
 * A context-(i.e. Level and, optionally, Player)-aware transformation of an object (built-in Conversions are for Items, ItemStacks, BlockStates, Blocks and Entities).
 * @see GoldConversion
 */
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

    public default <U, S extends U> boolean isChildInstance(U object, Class<S> subClass) {
        return object.getClass().isAssignableFrom(subClass);
    };

    public static <T> ConversionResult<T> convert(Level level, T object, @Nullable Player player, Collection<Conversion.Entry<T>> conversions) {
        final T original = object;
        for (Conversion.Entry<T> entry : conversions) {
            try {
                final ConversionResult<T> result = entry.conversion().convert(level, object, player);
                object = result.value();
                if (result.finish()) return result;
            } catch (Throwable e) {
                throw new RuntimeException("Problem while running converter " + entry.id().toString(), e);
            };
        };
        return object.equals(original) ? new ConversionResult.Pass<>(original) : new ConversionResult.Finish<>(object);
    };

    public static <T> NoConversion<T> dontConvert(Predicate<T> predicate) {
        return new NoConversion<>(predicate);
    };

    /**
     * Convert the Items of ItemStacks according to the given Item Conversion. The results are cached
     */
    public static ItemStackItemConversion convertItemStackItem(Conversion<Item> itemConversion) {
        return new ItemStackItemConversion(itemConversion);
    };

    /**
     * Convert the first Item into the second and finish
     */
    public static ReplacementConversion<Item> convertItem(Item item, Item result) {
        return convertItem(() -> item, () -> result);
    };

    /**
     * Convert the Item with the first ID (if it exists) to the Item with the second ID (if it exists) and finish, or else pass
     */
    public static ReplacementConversion<Item> convertItemIds(ResourceLocation id, ResourceLocation resultId) {
        return convertItem(Suppliers.memoize(() -> BuiltInRegistries.ITEM.get(id)), Suppliers.memoize(() -> BuiltInRegistries.ITEM.get(resultId)));
    };

    /**
     * Convert the first Item (if it exists) into the second (if it exists) and finish
     */
    public static ReplacementConversion<Item> convertItem(Supplier<Item> item, Supplier<Item> result) {
        return new ReplacementConversion<>(item, result);
    };

    /**
     * Convert all Items with the given Tag into the given Item, if they are the exact same class, and finish
     */
    public static TaggedItemConversion convertTaggedItemStrict(TagKey<Item> tag, Item item) {
        return convertTaggedItemStrict(tag, () -> item);
    };

    /**
     * Convert all Items with the given Tag into the given Item (if it exists), if they are the exact same class, and finish
     */
    public static TaggedItemConversion convertTaggedItemStrict(TagKey<Item> tag, Supplier<Item> item) {
        return new TaggedItemConversion(tag, item, true);
    };

    /**
     * Convert all Items whose IDs match the given regex into the given Item (if it exists), if they are the exact same class, and finish
     */
    public static ItemIDRegexConversion convertItemIdRegexStrict(String pathRegex, Supplier<Item> item) {
        return convertItemIdRegexStrict("^.*$", pathRegex, item);
    };

    /**
     * Convert all Items whose IDs match the given regexes into the given Item (if it exists), if they are the exact same class, and finish
     */
    public static ItemIDRegexConversion convertItemIdRegexStrict(String namespaceRegex, String pathRegex, Supplier<Item> item) {
        return new ItemIDRegexConversion(Pattern.compile(namespaceRegex), Pattern.compile(pathRegex), item, true);
    };

    /**
     * Convert all Items with the exact same class as the given Item into the given Item and finish
     */
    public static AlwaysConversion<Item> convertItemSameClass(Item item) {
        return convertItemSameClass(() -> item);
    };

    /**
     * Convert all Items with the exact same class as the given Item into the given Item and finish
     */
    public static AlwaysConversion<Item> convertItemSameClass(Supplier<Item> item) {
        return new AlwaysConversion<>(item, true);
    };

    /**
     * Apply the given Block Conversion to any Items which are Block Items and finish
     */
    public static BlockItemConversion convertBlockItem(Conversion<Block> blockConversion) {
        return new BlockItemConversion(blockConversion);
    };

    /**
     * Convert all BlockItems into the corresponding BlockItems of the Blocks converted with the given Block Conversion, and finish
     */
    public static BlockStateItemStackConversion convertItemStackBlockState(Conversion<BlockStateAndEntity> blockConversion) {
        return new BlockStateItemStackConversion(blockConversion);
    };

    /**
     * Apply the given transformation to the given Component, only if that Component already existed, and continue
     */
    public static <T> ItemStackComponentConversion<T> convertItemStackComponentIfPresent(DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemStackComponent(s -> s.has(component), component, map);
    };

    /**
     * Apply the given transformation to the given Component if the ItemStack passes the given predicate, and continue
     * @param <T> Component type
     * @see Conversion#convertItemStackComponentAndFinish(Predicate, DataComponentType, BiFunction) Finish instead
     */
    public static <T> ItemStackComponentConversion<T> convertItemStackComponent(Predicate<ItemStack> predicate, DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemStackComponent(predicate, () -> component, map, true);
    };

    /**
     * Apply the given transformation to the given Component if the ItemStack passes the given predicate, and finish
     * @param <T> Component type
     * @see Conversion#convertItemStackComponent(Predicate, DataComponentType, BiFunction) Continue instead
     */
    public static <T> ItemStackComponentConversion<T> convertItemStackComponentAndFinish(Predicate<ItemStack> predicate, DataComponentType<T> component, BiFunction<Level, T, T> map) {
        return convertItemStackComponent(predicate, () -> component, map, false);
    };

    /**
     * Apply the given transformation to the given Component if the ItemStack passes the given predicate
     * @param <T> Component type
     */
    public static <T> ItemStackComponentConversion<T> convertItemStackComponent(Predicate<ItemStack> predicate, Supplier<DataComponentType<T>> component, BiFunction<Level, T, T> map, boolean pass) {
        return new ItemStackComponentConversion<>(predicate, component, map, pass);
    };

    /**
     * Apply the given ItemStack Conversion to all Container contents or BundleContents of the ItemStack, and continue
     */
    public static ItemStackContainerConversion convertItemStackContainerContents(Conversion<ItemStack> contentsConversion) {
        return new ItemStackContainerConversion(contentsConversion);
    };

    /**
     * Convert the Blocks of BlockStates according to the given Block Conversion. The results are cached
     */
    public static BlockStateBlockConversion convertBlockStateBlock(Conversion<Block> blockConversion) {
        return new BlockStateBlockConversion(blockConversion);
    };

    /**
     * Convert the first Block into the second and finish
     */
    public static ReplacementConversion<Block> convertBlock(Block block, Block result) {
        return convertBlock(() -> block, () -> result);
    };

    /**
     * Convert the Block with the first ID (if it exists) to the Block with the second ID (if it exists) and finish, or else pass
     */
    public static ReplacementConversion<Block> convertBlockIds(ResourceLocation id, ResourceLocation resultId) {
        return convertBlock(BlockHelper.supplier(id), BlockHelper.supplier(resultId));
    };

    /**
     * Convert the first Block into the second and finish
     */
    public static ReplacementConversion<Block> convertBlock(Supplier<Block> block, Supplier<Block> result) {
        return new ReplacementConversion<>(block, result);
    };

    /**
     * Convert all Blocks with the given Tag into the given Block, if they are the exact same class, and finish
     */
    public static TaggedBlockConversion convertTaggedBlockStrict(TagKey<Block> tag, Block block) {
        return convertTaggedBlockStrict(tag, () -> block);
    };

    /**
     * Convert all Blocks with the given Tag into the Block with the given ID (if it exists), if they are a subclass of the class of that Block, and finish
     */
    public static TaggedBlockConversion convertTaggedBlock(TagKey<Block> tag, ResourceLocation blockId) {
        return convertTaggedBlock(tag, BlockHelper.supplier(blockId));
    };

    /**
     * Convert all Blocks with the given Tag into the given Block (if it exists), if they are a subclass of the class of that Block, and finish
     */
    public static TaggedBlockConversion convertTaggedBlock(TagKey<Block> tag, Supplier<Block> block) {
        return new TaggedBlockConversion(tag, block, false);
    };

    /**
     * Convert all Blocks of the given Tag into the given Block (if it exists), if they are the exact same class, and finish
     */
    public static TaggedBlockConversion convertTaggedBlockStrict(TagKey<Block> tag, Supplier<Block> block) {
        return new TaggedBlockConversion(tag, block, true);
    };

    /**
     * Convert all Blocks whose ID paths match the given regex into the given Block, if they are the exact same class, and finish
     */
    public static BlockIDRegexConversion convertBlockIdRegexStrict(String regex, Block block) {
        return convertBlockIdRegexStrict(regex, () -> block);
    };

    /**
     * Convert all Blocks whose ID paths match the given regex into the given Block (if it exists), if they are the exact same class, and finish
     */
    public static BlockIDRegexConversion convertBlockIdRegexStrict(String regex, Supplier<Block> block) {
        return convertBlockIdRegexStrict("^.*$", regex, block);
    };

    /**
     * Convert all Blocks whose ID paths match the given regex into the given Block (if it exists), and finish
     */
    public static BlockIDRegexConversion convertBlockIdRegex(String regex, Supplier<Block> block) {
        return convertBlockIdRegex("^.*$", regex, block);
    };

    /**
     * Convert all Blocks whose IDs match the given regexes into the given Block (if it exists), if they are the exact same class, and finish
     */
    public static BlockIDRegexConversion convertBlockIdRegexStrict(String namespaceRegex, String pathRegex, Supplier<Block> block) {
        return new BlockIDRegexConversion(Pattern.compile(namespaceRegex), Pattern.compile(pathRegex), block, true);
    };

    /**
     * Convert all Blocks whose IDs match the given regexes into the given Block (if it exists) and finish
     */
    public static BlockIDRegexConversion convertBlockIdRegex(String namespaceRegex, String pathRegex, Supplier<Block> block) {
        return new BlockIDRegexConversion(Pattern.compile(namespaceRegex), Pattern.compile(pathRegex), block, false);
    };

    /**
     * Convert all Blocks with the exact same class as the given Block into the given Block and finish
     */
    public static AlwaysConversion<Block> convertBlockSameClass(Block block) {
        return convertBlockSameClass(() -> block);
    };

    /**
     * Convert all Blocks with the exact same class as the given Block into the given Block (if it exists) and finish
     */
    public static AlwaysConversion<Block> convertBlockSameClass(Supplier<Block> block) {
        return new AlwaysConversion<>(block, true);  
    };

    /**
     * If the Block is a Container, convert all ItemStacks in it according to the given ItemStack Conversion, and continue
     */
    public static ContainerConversion convertContainerContents(Conversion<ItemStack> contentsConversion) {
        return new ContainerConversion(contentsConversion);
    };

    public record NoConversion<T>(Predicate<T> predicate) implements Conversion<T> {

        @Override
        public ConversionResult<T> convert(Level level, T object, @Nullable Player player) {
            return predicate().test(object) ? finish(object) : pass(object);
        };

    };

    public record ReplacementConversion<T>(Supplier<T> object, Supplier<T> result) implements Conversion<T> {

        @Override
        public ConversionResult<T> convert(Level level, T object, @Nullable Player player) {
            return result().get() != null && Objects.equals(object().get(), object) ? finish(result().get()) : pass(object);
        };

    };

    public record AlwaysConversion<T>(Supplier<T> result, boolean classesMustMatch) implements Conversion<T> {

        @Override
        public ConversionResult<T> convert(Level level, T object, @Nullable Player player) {
            return result().get() != null && (classesMustMatch() ? result().get().getClass() == object.getClass() : object.getClass().isAssignableFrom(result().get().getClass())) ? finish(result().get()) : pass(object);
        };

    };

    public record TaggedItemConversion(TagKey<Item> tag, Supplier<Item> result, boolean classesMustMatch) implements Conversion<Item> {

        @Override
        @SuppressWarnings("deprecation")
        public ConversionResult<Item> convert(Level level, Item object, @Nullable Player player) {
            return result().get() != null && object.builtInRegistryHolder().is(tag()) && (object.getClass() == result().getClass() || !classesMustMatch()) ? finish(result().get()) : pass(object);
        };
    };

    public record ItemIDRegexConversion(Pattern namespaceRegex, Pattern pathRegex, Supplier<Item> result, boolean classesMustMatch) implements Conversion<Item> {

        @Override
        public ConversionResult<Item> convert(Level level, Item object, @Nullable Player player) {
            final ResourceLocation id = BuiltInRegistries.ITEM.getKey(object);
            return result().get() != null && (result().get().getClass() == object.getClass() || !classesMustMatch()) && namespaceRegex().matcher(id.getNamespace()).matches() && pathRegex().matcher(id.getPath()).matches() ? finish(result().get()) : pass(object);
        };

    };

    public record BlockItemConversion(Conversion<Block> blockConversion) implements Conversion<Item> {

        @Override
        public ConversionResult<Item> convert(Level level, Item object, @Nullable Player player) {
            if (object instanceof BlockItem blockItem) {
                final Block resultBlock = blockConversion().convert(level, blockItem.getBlock(), player).value();
                final Item resultItem = resultBlock.asItem();
                if (
                    resultItem != Items.AIR
                    && resultBlock != blockItem.getBlock()
                    && resultItem instanceof BlockItem resultBlockItem
                    && resultBlockItem.getBlock() == resultBlock
                ) return finish(resultItem);
            };
            return pass(object);
        };
        
    };

    public abstract class TieredItemConversion implements Conversion<Item> {

        protected final Map<Class<? extends TieredItem>, Map<Item, Optional<TieredItem>>> map = new HashMap<>();

        public abstract Tier convertTier(Level level, Item item, Tier tier);

        @Override
        public ConversionResult<Item> convert(Level level, Item object, @Nullable Player player) {
            if (object instanceof TieredItem tieredItem) {
                final Class<? extends TieredItem> tieredItemClass = tieredItem.getClass();
                return map.computeIfAbsent(tieredItemClass, $ -> new HashMap<>())
                    .computeIfAbsent(tieredItem, $ -> {
                        final Tier tier = convertTier(level, object, tieredItem.getTier());
                        return BuiltInRegistries.ITEM.stream()
                            .filter(item -> item.getClass() == tieredItemClass)
                            .map(item -> (TieredItem)item)
                            .filter(item -> item.getTier() == tier)
                            .findFirst();
                    }).<ConversionResult<Item>>map(item -> finish(item)).orElse(pass(object));
            };
            return pass(object);
        };

    };

    public abstract class ArmorItemConversion implements Conversion<Item> {

        protected final Map<ArmorItem.Type, Map<Item, Optional<ArmorItem>>> map = new HashMap<>();

        public abstract Holder<ArmorMaterial> convertArmorMaterial(Level level, Item object, Holder<ArmorMaterial> tier);

        @Override
        public ConversionResult<Item> convert(Level level, Item object, @Nullable Player player) {
            if (object instanceof ArmorItem armorItem) {
                return map.computeIfAbsent(armorItem.getType(), $ -> new HashMap<>())
                    .computeIfAbsent(armorItem, $ -> {
                        final Holder<ArmorMaterial> armorMaterial = convertArmorMaterial(level, object, armorItem.getMaterial());
                        return BuiltInRegistries.ITEM.stream()
                            .filter(item -> item instanceof ArmorItem armorItem2 && armorItem2.getType() == armorItem.getType())
                            .map(item -> (ArmorItem)item)
                            .filter(item -> item.getMaterial() == armorMaterial)
                            .findFirst();
                    }).<ConversionResult<Item>>map(item -> finish(item)).orElse(pass(object));
            };
            return pass(object);
        };
    };

    public record TaggedBlockConversion(TagKey<Block> tag, Supplier<Block> result, boolean classesMustMatch) implements Conversion<Block> {
        
        @Override
        @SuppressWarnings("deprecation")
        public ConversionResult<Block> convert(Level level, Block object, @Nullable Player player) {
            return result().get() != null && object.builtInRegistryHolder().is(tag()) && (object.getClass() == result().getClass() || !classesMustMatch()) ? finish(result().get()) : pass(object);
        };
    };

    public record BlockIDRegexConversion(Pattern namespaceRegex, Pattern pathRegex, Supplier<Block> result, boolean classesMustMatch) implements Conversion<Block> {

        @Override
        public ConversionResult<Block> convert(Level level, Block object, @Nullable Player player) {
            final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(object);
            return result().get() != null && (result().get().getClass() == object.getClass() || !classesMustMatch()) && namespaceRegex().matcher(id.getNamespace()).matches() && pathRegex().matcher(id.getPath()).matches() ? finish(result().get()) : pass(object);
        };

    };

    public interface ItemStackConversion extends Conversion<ItemStack> {

        public default ConversionResult<ItemStack> swap(ItemStack stack, Item item) {
            return swap(stack, item, true);
        };

        public default ConversionResult<ItemStack> swap(ItemStack stack, Item item, boolean classesMustMatch) {
            if (classesMustMatch && !isInstance(stack.getItem(), item.getClass())) return pass(stack);
            final ItemStack result = new ItemStack(item, stack.getCount());
            result.applyComponents(stack.getComponentsPatch());
            return finish(result);
        };
    };

    public final class ItemStackItemConversion implements ItemStackConversion {

        private final Map<Item, Item> map = new ConcurrentHashMap<>();

        public final Conversion<Item> itemConversion;

        public ItemStackItemConversion(Conversion<Item> itemConversion) {
            this.itemConversion = itemConversion;
        };

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            final Item item = map.computeIfAbsent(object.getItem(), $ -> itemConversion.convert(level, object.getItem(), player).value());
            return object.getItem() == item ? pass(object) : swap(object, item, false);
        };

    };

    public record BlockStateItemStackConversion(Conversion<BlockStateAndEntity> blockConversion) implements ItemStackConversion {

        @Override
        public ConversionResult<ItemStack> convert(Level level, ItemStack object, @Nullable Player player) {
            if (object.getItem() instanceof BlockItem blockItem && object.has(DataComponents.BLOCK_STATE)) {
                final Item item = blockConversion().convert(level, new BlockStateAndEntity(object.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState()), null), player).value().state().getBlock().asItem();
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
                        .map(stack -> contentsConversion().convert(level, stack, player).value())
                        .toList()
                ));
            };
            if (object.has(DataComponents.BUNDLE_CONTENTS)) {
                object.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(
                    object.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).itemCopyStream()
                        .map(stack -> contentsConversion().convert(level, stack, player).value())
                        .toList()
                ));
            };
            return pass(object);
        };
    };

    public interface BlockStateConversion extends Conversion<BlockStateAndEntity> {

        public default ConversionResult<BlockStateAndEntity> swap(BlockStateAndEntity blockAndEntity, Block block) {
            return swap(blockAndEntity, block, true);
        };

        public default ConversionResult<BlockStateAndEntity> swap(BlockStateAndEntity blockAndEntity, Block block, boolean classesMustMatch) {
            if (!isInstance(blockAndEntity.block(), block.getClass()) && classesMustMatch) return pass(blockAndEntity);
            return blockAndEntity.withStateOptional(BlockHelper.copyAll(block.defaultBlockState(), blockAndEntity.state()))
                .<ConversionResult<BlockStateAndEntity>>map(this::finish)
                .orElseGet(supplyPass(blockAndEntity));
        };
    };

    public final class BlockStateBlockConversion implements BlockStateConversion {

        private final Map<Block, Block> map = new ConcurrentHashMap<>();

        public final Conversion<Block> blockConversion;

        public BlockStateBlockConversion(Conversion<Block> blockConversion) {
            this.blockConversion = blockConversion;
        };

        @Override
        public ConversionResult<BlockStateAndEntity> convert(Level level, BlockStateAndEntity object, @Nullable Player player) {
            final Block block = map.computeIfAbsent(object.block(), $ -> blockConversion.convert(level, object.block(), player).value());
            return block == object.block() ? pass(object) : swap(object, block, false);
        };
        
    };

    public record ContainerConversion(Conversion<ItemStack> itemConversion) implements BlockStateConversion {

        @Override
        public ConversionResult<BlockStateAndEntity> convert(Level level, BlockStateAndEntity object, @Nullable Player player) {
            object.entityOp().ifPresent(be -> {
                if (be instanceof Container container) {
                    for (int slot = 0; slot < container.getContainerSize(); slot++) {
                        final ItemStack stack = container.getItem(slot);
                        final ItemStack converted = itemConversion().convert(level, stack, player).value();
                        if (!ItemStack.isSameItemSameComponents(stack, converted)) container.setItem(slot, converted);
                    };
                };
            });
            return pass(object);
        };

    };

    public static abstract class DyedBlockConversion implements Conversion<Block> {

        protected final Map<Block, Optional<Block>> blocks = new HashMap<>();

        public abstract DyeColor convert(Block block, DyeColor color);

        @Override
        public ConversionResult<Block> convert(Level level, Block object, @Nullable Player player) {
            if (!DyeHelper.isDyed(object)) return pass(object);
            final Optional<Block> block = blocks.computeIfAbsent(object, b -> {
                final Optional<BiMap<DyeColor, Block>> map = DyeHelper.getMap(b);
                if (map.isEmpty()) return Optional.empty(); // Not dyed
                return Optional.of(map.get().get(convert(object, map.get().inverse().get(b))));
            });
            return block.isEmpty() ? pass(object) : finish(block.get()); 
        };

    };

    public record ItemEntityConversion(Conversion<ItemStack> itemStackConversion) implements Conversion<Entity> {

        @Override
        public ConversionResult<Entity> convert(Level level, Entity object, @Nullable Player player) {
            if (object instanceof ItemEntity itemEntity) {
                itemEntity.setItem(itemStackConversion().convert(level, itemEntity.getItem(), player).value());
                return finish(itemEntity);
            } else {
                return pass(object);
            }
        };

    };

    public record InventoryEntityConversion(Conversion<ItemStack> itemStackConversion) implements Conversion<Entity> {

        @Override
        public ConversionResult<Entity> convert(Level level, Entity object, @Nullable Player player) {
            if (object instanceof LivingEntity living) ItemHelper.modifyItems(living, stack -> itemStackConversion().convert(level, stack, player).value());
            return pass(object);
        };

    };

    public abstract class ColorEntityConversion implements Conversion<Entity> {

        public abstract DyeColor convert(Entity entity, DyeColor color);

        @Override
        public ConversionResult<Entity> convert(Level level, Entity object, @Nullable Player player) {
            if (object instanceof LivingEntity living) {
                ColorHelper.setColor(living, convert(object, ColorHelper.getColor(living, false)), false);
                ColorHelper.setColor(living, convert(object, ColorHelper.getColor(living, true)), true);
            };
            return pass(object);
        };
    };

    public record ItemFrameItemConversion(Conversion<ItemStack> itemConversion) implements Conversion<Entity> {

        @Override
        public ConversionResult<Entity> convert(Level level, Entity object, @Nullable Player player) {
            if (object instanceof ItemFrame itemFrame) {
                itemFrame.setItem(itemConversion().convert(level, itemFrame.getItem(), player).value());
                return finish(itemFrame);
            } else {
                return pass(object);
            }
        };

    };

    public sealed interface ConversionResult<T> permits ConversionResult.Pass, ConversionResult.Finish {

        public T value();

        public boolean pass();

        public default boolean finish() {
            return !pass();
        };

        public static record Pass<T>(T value) implements ConversionResult<T> {

            @Override
            public boolean pass() {
                return true;
            };
        };

        public static record Finish<T>(T value) implements ConversionResult<T> {

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
