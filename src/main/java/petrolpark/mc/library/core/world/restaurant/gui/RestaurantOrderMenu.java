package petrolpark.mc.library.core.world.restaurant.gui;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;

@ParametersAreNonnullByDefault
public class RestaurantOrderMenu extends AbstractContainerMenu {

    public final @Nullable ICustomer customer;

    public RestaurantOrderMenu(MenuType<RestaurantOrderMenu> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, null);
    };

    protected RestaurantOrderMenu(MenuType<RestaurantOrderMenu> menuType, int containerId, Inventory playerInventory, @Nullable ICustomer customer) {
        super(menuType, containerId);
        this.customer = customer;

        // Inventory Slots
        final int inventoryX = 51;
        final int inventoryY = 107;
        for (int row = 0; row < 3; ++row)
			for (int col = 0; col < 9; ++col)
				addSlot(new Slot(playerInventory, col + row * 9 + 9, inventoryX + col * 18, inventoryY + row * 18));
		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
			addSlot(new Slot(playerInventory, hotbarSlot, inventoryX + hotbarSlot * 18, inventoryY + 58));
    };

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; //TODO
    };

    @Override
    @SuppressWarnings("null")
    public boolean stillValid(Player player) {
        return customer == null || customer.canInteractWith(player);
    };
    
};
