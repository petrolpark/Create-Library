package petrolpark.mc.library.core.world.item;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class StackCompressibleItem extends Item {

    public final ItemLike compressedItem;

    public StackCompressibleItem(ItemLike nextCoinItem, Item.Properties properties) {
        super(properties);
        this.compressedItem = nextCoinItem;
    };
    
    //TODO split into subdenomination

    @Override
    public boolean overrideOtherStackedOnMe(@Nonnull ItemStack stack, @Nonnull ItemStack other, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player, @Nonnull SlotAccess otherSlotAccess) {
        final int maxStackSize = getMaxStackSize(stack);
        if (other.getItem() == this && slot.allowModification(player) && (action == ClickAction.PRIMARY || stack.getCount() >= maxStackSize)) {
            final int total = stack.getCount() + other.getCount();
            if (total > maxStackSize) {
                slot.set(new ItemStack(compressedItem));
                other.setCount(total - maxStackSize - 1);
                return true;
            };
        } else if (other.getItem() == compressedItem.asItem() && other.getCount() == 1 && stack.getCount() < maxStackSize) {
            final int oldCount = stack.getCount();
            stack.setCount(maxStackSize);
            otherSlotAccess.set(new ItemStack(this, oldCount + 1));
            return true;
        };
        return false;
    };
    
};
