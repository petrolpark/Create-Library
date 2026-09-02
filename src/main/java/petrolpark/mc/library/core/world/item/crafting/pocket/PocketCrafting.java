package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.world.inventory.DummySlot;
import petrolpark.mc.library.util.ItemHelper;

public class PocketCrafting {
    
    public static Pair<Int2ObjectMap<Int2ObjectMap<Slot>>, Int2ObjectMap<SlotGrid>> organiseSlots(AbstractContainerMenu menu, Collection<Slot> slots, SlotArrangement backgroundSlots) {

        final Int2ObjectMap<Int2ObjectMap<Slot>> positionedSlots = new Int2ObjectArrayMap<>();
        positionedSlots.defaultReturnValue(Int2ObjectMaps.emptyMap());
        for (final Slot slot : slots) {
            if (slot.isFake()) continue;
            positionedSlots.computeIfAbsent(slot.y, $ -> new Int2ObjectArrayMap<>()).put(slot.x, slot);
        };

        final Int2ObjectMap<SlotGrid> grids = new Int2ObjectArrayMap<>();

        // Sorted top-to-bottom, left-to-right, so the next seed is always the top-left corner of whatever is left
        final NavigableSet<Slot> unvisitedSlots = new TreeSet<>(SLOT_COMPARATOR);
        unvisitedSlots.addAll(slots);
        while (!unvisitedSlots.isEmpty()) {
            final Slot slot = unvisitedSlots.pollFirst();

            List<Slot> row = new ArrayList<>();
            final SlotGrid grid = new SlotGrid();
            row.add(slot);
            grid.slots.add(slot);
            grids.put(ItemHelper.getActualIndex(menu, slot), grid);

            // Expand as far to the right as possible
            Slot rightSlot = getOrBackground(slot.x + 18, slot.y, menu, positionedSlots, backgroundSlots);
            while (rightSlot != null) {
                unvisitedSlots.remove(rightSlot);
                row.add(rightSlot);
                grid.width++;
                grid.slots.add(rightSlot);
                grids.put(ItemHelper.getActualIndex(menu, rightSlot), grid);
                rightSlot = getOrBackground(rightSlot.x + 18, slot.y, menu, positionedSlots, backgroundSlots);
            };

            addNewRows: while (true) {
                final List<Slot> nextRow = new ArrayList<>();
                for (Slot slotInRow : row) {
                    final Slot belowSlot = getOrBackground(slotInRow.x, slotInRow.y + 18, menu, positionedSlots, backgroundSlots);
                    if (belowSlot == null) break addNewRows;
                    nextRow.add(belowSlot);
                };
                for (Slot belowSlot : nextRow) {
                    unvisitedSlots.remove(belowSlot);
                    grid.slots.add(belowSlot);
                    grids.put(ItemHelper.getActualIndex(menu, belowSlot), grid);
                };
                row = nextRow;
            };
        };

        return Pair.of(positionedSlots, grids);
    };

    @Nullable
    public static Slot getOrBackground(int x, int y, AbstractContainerMenu menu, Int2ObjectMap<Int2ObjectMap<Slot>> slots, SlotArrangement backgroundSlots) {
        Slot slot = slots.get(y).get(x);
        if (slot != null) return slot;
        slot = backgroundSlots.get(x, y);
        return slot == null ? null : new DummySlot(slot.container, ItemHelper.getActualIndex(menu, slot), slot.x, slot.y);
    };
    
    public static final Comparator<Slot> SLOT_COMPARATOR = new Comparator<>(){

        @Override
        public int compare(Slot arg0, Slot arg1) {
            final int y = arg0.y - arg1.y;
            return y == 0 ? arg0.x - arg1.x : y;
        };

    };

    @FunctionalInterface
    public interface SlotArrangement {

        public static final SlotArrangement NONE = (x, y) -> null;

        public @Nullable Slot get(int x, int y);
    };

    public static class SlotGrid {

        protected int width = 1;
        protected List<Slot> slots = new ArrayList<>();

        public int width() {
            return width;
        };

        public Stream<Slot> streamSlots() {
            return slots.stream();
        };
    };

    public interface Result {

        public static final Result FAIL = new Result() {

            @Override
            public boolean successful() {
                return false;
            };

        };

        public boolean successful();

        @OnlyIn(Dist.CLIENT)
        public default List<? extends PocketCrafting.Output> outputs() {
            return Collections.emptyList();
        };

        @OnlyIn(Dist.CLIENT)
        public default void addToTooltip(Consumer<Component> tooltip) {};
    };

    @OnlyIn(Dist.CLIENT)
    public interface Output {

        public void render(GuiGraphics graphics);
    };
};
