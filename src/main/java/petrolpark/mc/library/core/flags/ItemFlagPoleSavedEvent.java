package petrolpark.mc.library.core.flags;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class ItemFlagPoleSavedEvent extends Event {
    
    public final ItemStack stack;
    public final ItemFlagPole flags;

    public ItemFlagPoleSavedEvent(ItemStack stack, ItemFlagPole flags) {
        this.stack = stack;
        this.flags = flags;
    };
};
