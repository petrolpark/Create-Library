package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.inventory.Slot;

public class PocketCrafting {
    
    public static Pair<Int2ObjectMap<Int2ObjectMap<Slot>>, Int2ObjectMap<SlotGrid>> organiseSlots(Collection<Slot> slots) {
        final Int2ObjectMap<Int2ObjectMap<Slot>> positionedSlots = new Int2ObjectArrayMap<>();
        positionedSlots.defaultReturnValue(Int2ObjectMaps.emptyMap());
        for (final Slot slot : slots) {
            if (slot.isFake()) continue;
            positionedSlots.computeIfAbsent(slot.y, $ -> new Int2ObjectArrayMap<>()).put(slot.x, slot);
        };

        final Int2ObjectMap<SlotGrid> grids = new Int2ObjectArrayMap<>();

        final Queue<Slot> unsortedSlots = new LinkedList<>(slots);
        while (!unsortedSlots.isEmpty()) {
            final Slot slot = unsortedSlots.poll();

            List<Slot> row = new ArrayList<>();
            final SlotGrid grid = new SlotGrid();
            row.add(slot);
            grid.slots.add(slot);
            grids.put(slot.index, grid);

            // Expand as far to the right as possible
            Slot rightSlot = positionedSlots.get(slot.y).get(slot.x + 18);
            while (rightSlot != null) {
                unsortedSlots.remove(rightSlot);
                row.add(rightSlot);
                grid.width++;
                grid.slots.add(rightSlot);
                grids.put(rightSlot.index, grid);
                rightSlot = positionedSlots.get(slot.y).get(rightSlot.x + 18);
            };

            addNewRows: while (true) {
                final List<Slot> nextRow = new ArrayList<>();
                // Check there is an entire next row
                for (Slot slotInRow : row) {
                    final Slot belowSlot = positionedSlots.get(slotInRow.y + 18).get(slotInRow.x);
                    if (belowSlot == null) break addNewRows;
                    nextRow.add(belowSlot);
                };
                // Add everything in that row
                for (Slot belowSlot : nextRow) {
                    unsortedSlots.remove(belowSlot);
                    grid.slots.add(belowSlot);
                    grids.put(belowSlot.index, grid);
                };
                row = nextRow;
            };
        };

        return Pair.of(positionedSlots, grids);
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
    };
};
