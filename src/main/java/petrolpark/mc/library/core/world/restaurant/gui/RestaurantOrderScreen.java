package petrolpark.mc.library.core.world.restaurant.gui;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@ParametersAreNonnullByDefault
public class RestaurantOrderScreen extends AbstractContainerScreen<RestaurantOrderMenu> {

    public RestaurantOrderScreen(RestaurantOrderMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    };

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
    };
    
};
