package petrolpark.mc.library.registry;

import com.tterrag.registrate.util.entry.MenuEntry;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.restaurant.gui.RestaurantOrderMenu;
import petrolpark.mc.library.core.world.restaurant.gui.RestaurantOrderScreen;

public class PetrolparkMenuTypes {
  
    public static final MenuEntry<RestaurantOrderMenu> RESTAURANT_ORDER = Petrolpark.REGISTRATE.menu("restaurant_order", RestaurantOrderMenu::new, () -> RestaurantOrderScreen::new)
        .register();

    public static final void register() {};
};
