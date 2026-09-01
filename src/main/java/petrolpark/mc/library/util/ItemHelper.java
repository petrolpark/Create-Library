package petrolpark.mc.library.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;

public class ItemHelper {

    public static final <T> Optional<T> getOptional(ItemStack stack, DataComponentType<T> componentType) {
        return Optional.ofNullable(stack.get(componentType));
    };

    public static final boolean equalIgnoringComponents(ItemStack stack1, ItemStack stack2, DataComponentType<?> ...ignoredComponentTypes) {
        return equalIgnoringComponents(stack1, stack2, type -> Stream.of(ignoredComponentTypes).anyMatch(type::equals));
    };

    public static final boolean equalIgnoringComponents(ItemStack stack1, ItemStack stack2, TagKey<DataComponentType<?>> ignoredComponentTypesTag) {
        return equalIgnoringComponents(stack1, stack2, type -> BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(type).is(ignoredComponentTypesTag));
    };

    public static final boolean equalIgnoringComponents(ItemStack stack1, ItemStack stack2, Predicate<DataComponentType<?>> ignoredComponentTypes) {
        ItemStack trueStack1 = ItemDecay.checkDecay(stack1);
        ItemStack trueStack2 = ItemDecay.checkDecay(stack2);
        if (!trueStack1.is(trueStack2.getItem())) return false;
        if (trueStack1.isEmpty()) return trueStack2.isEmpty();
        if (Objects.equals(trueStack1.getComponents(), trueStack2.getComponents())) return true; // Return early to avoid deep-checking all components unnecessarily
        return DataComponentHelper.equalIgnoring(trueStack1.getComponents(), trueStack2.getComponents(), ignoredComponentTypes);
    };

    public static final ItemEntity getItemEntityToPop(Level level, BlockPos pos, ItemStack stack) {
        double halfHeight = (double)EntityType.ITEM.getHeight() / 2d;
        double x = (double)pos.getX() + 0.5d + Mth.nextDouble(level.random, -0.25d, 0.25d);
        double y = (double)pos.getY() + 0.5d + Mth.nextDouble(level.random, -0.25d, 0.25d) - halfHeight;
        double z = (double)pos.getZ() + 0.5d + Mth.nextDouble(level.random, -0.25d, 0.25d);
        return new ItemEntity(level, x, y, z, stack);
    };

    public static final void pop(Level level, Vec3 position, ItemStack stack) {
        if (level.isClientSide()) return;
        ItemEntity entity = new ItemEntity(level, position.x, position.y, position.z, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    };
    
    /**
     * @param inv
     * @param test
     * @param maxCount
     * @return Actual number of actual Items removed
     */
    public static final int removeItems(IItemHandler inv, Predicate<ItemStack> test, int maxCount) {
        int removed = 0;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            if (test.test(inv.getStackInSlot(slot))) removed += inv.extractItem(slot, maxCount - removed, false).getCount();
        };
        return removed;
    };

    public static final ItemStack removeItem(IItemHandler inv, Predicate<ItemStack> test, boolean simulate) {
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            if (test.test(inv.getStackInSlot(slot))) return inv.extractItem(slot, 1, simulate);
        };
        return ItemStack.EMPTY;
    };

    public static final void give(Entity entity, Stream<ItemStack> stacks) {
        if (entity instanceof InventoryCarrier hasInv) {
            stacks.forEach(stack -> entity.spawnAtLocation(ItemHandlerHelper.insertItemStacked(new InvWrapper(hasInv.getInventory()), stack, false)));
        } else if (entity instanceof Player player) {
            stacks.forEach(player.getInventory()::placeItemBackInInventory);
        } else {
            stacks.forEach(entity::spawnAtLocation);
        };
    };

    public static final void modifyItems(LivingEntity livingEntity, UnaryOperator<ItemStack> function) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (livingEntity.hasItemInSlot(slot)) livingEntity.setItemSlot(slot, function.apply(livingEntity.getItemBySlot(slot)));
        };
        Container inv = null;
        if (livingEntity instanceof InventoryCarrier inventoryCarrier) inv = inventoryCarrier.getInventory();
        if (livingEntity instanceof Player otherPlayer) inv = otherPlayer.getInventory();
        if (inv != null) {
            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                final ItemStack stack = inv.getItem(slot);
                if (stack.isEmpty()) continue;
                inv.setItem(slot, function.apply(stack));
            };
        };
    };

    /**
     * Very crude prediction of what a Loot Table might give
     * @param lootTable
     */
    public static final Stream<ItemStack> streamPossibleItems(LootTable lootTable) {
        return lootTable.pools.stream().flatMap(pool -> pool.entries.stream()).flatMap(entry -> {
            if (entry instanceof LootItem lootItem) return Stream.of(new ItemStack(lootItem.item));
            if (entry instanceof NestedLootTable nestedTable) return nestedTable.contents.right().stream().flatMap(ItemHelper::streamPossibleItems);
            return Stream.empty();
        });
    };
    
    public static void insertStartingWithSlot(Inventory inventory, ItemStack stack, int startingSlot) {
        int slot = startingSlot;
        while (true) {
            if (slot >= inventory.items.size()) slot = 0;
            if (slot == startingSlot) {
                inventory.player.drop(stack, false);
                return;
            };
            if (inventory.add(slot, stack)) return;
        }
    };

    @Deprecated
    public static final Set<Item> getKnownAnvilRepairItems() {
        return Collections.emptySet();
        //TODO rework example items for pquality
    };

    private static Set<Item> KNOWN_ANIMAL_FOODS = null;

    public static final Set<Item> getKnownAnimalFoods(Level level) {
        if (KNOWN_ANIMAL_FOODS == null) {
            final List<Animal> animals = BuiltInRegistries.ENTITY_TYPE.stream()
                .<Animal>mapMulti((type, consumer) -> { if (type.create(level) instanceof Animal animal) consumer.accept(animal); })
                .toList();
            KNOWN_ANIMAL_FOODS = BuiltInRegistries.ITEM.stream()
                .map(ItemStack::new)
                .filter(stack -> animals.stream().anyMatch(animal -> animal.isFood(stack)))
                .map(ItemStack::getItem)
                .collect(Collectors.toUnmodifiableSet());
        };
        return KNOWN_ANIMAL_FOODS;
    };
};
