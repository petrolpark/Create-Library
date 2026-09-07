package petrolpark.mc.library.core.world.item.deletable;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Item that can be deleted from the Inventory by holding x
 */
@ParametersAreNonnullByDefault
public interface IDeletableItem {
    
    /**
     * If something additional can be done to the ItemStack to discard it
     * @param player
     * @param stack
     */
    public default void delete(Player player, ItemStack stack) {

    };
};
