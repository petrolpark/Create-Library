package petrolpark.mc.library.core.world.inventory;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record DummyContainer(int size) implements Container {

    public static final void addFakePlayerSlots(Consumer<Slot> slotAdder) {
        final DummyContainer container = new DummyContainer(36);
        for (int i = 0; i < 36; i++) slotAdder.accept(new DummySlot(container, i));
    };

    @Override
    public void clearContent() {};

    @Override
    public int getContainerSize() {
        return size();
    };

    @Override
    public boolean isEmpty() {
        return true;
    };

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    };

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    };

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    };

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {};

    @Override
    public void setChanged() {};

    @Override
    public int getMaxStackSize() {
        return 0;
    };

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return false;
    };

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return false;
    };

    @Override
    public boolean canTakeItem(@Nonnull Container target, int slot, @Nonnull ItemStack stack) {
        return false;
    };

    @Override
    public int countItem(@Nonnull Item item) {
        return 0;
    };

    @Override
    public boolean hasAnyOf(@Nonnull Set<Item> set) {
        return false;
    };

    @Override
    public boolean hasAnyMatching(@Nonnull Predicate<ItemStack> predicate) {
        return false;
    };
    
};
