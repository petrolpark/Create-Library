package petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record ItemInterpretedSlot(Slot slot) implements IInterpretedSlot {

    public ItemStack ingredient() {
        return slot().getItem();
    };
};
