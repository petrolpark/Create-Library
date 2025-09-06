package com.petrolpark.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.petrolpark.core.item.decay.ItemDecay;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

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

    public static final void give(Entity entity, Stream<ItemStack> stacks) {
        if (entity instanceof InventoryCarrier hasInv) {
            stacks.forEach(stack -> entity.spawnAtLocation(ItemHandlerHelper.insertItemStacked(new InvWrapper(hasInv.getInventory()), stack, false)));
        } else if (entity instanceof Player player) {
            stacks.forEach(player.getInventory()::placeItemBackInInventory);
        } else {
            stacks.forEach(entity::spawnAtLocation);
        };
    };
};
