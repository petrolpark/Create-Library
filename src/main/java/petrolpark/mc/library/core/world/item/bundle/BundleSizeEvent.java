package petrolpark.mc.library.core.world.item.bundle;

import org.apache.commons.lang3.math.Fraction;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class BundleSizeEvent extends Event implements ICancellableEvent {
    
    protected Fraction size = Fraction.ONE;
    protected final ItemStack stack;

    public BundleSizeEvent(ItemStack stack) {
        this.stack = stack;
    };

    public Fraction getSize() {
        return size;
    };

    public void setSize(Fraction size) {
        this.size = size;
    };

    public ItemStack getStack() {
        return stack;
    };
};
