package petrolpark.mc.library.core.world.restaurant.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class RestaurantOrderMenu extends AbstractContainerMenu {

    protected RestaurantOrderMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        //TODO Auto-generated constructor stub
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'quickMoveStack'");
    }

    @Override
    public boolean stillValid(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
    }
    
};
