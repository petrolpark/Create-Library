package petrolpark.mc.library.core.client.tooltip;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public class RenderTooltipEventPost extends RenderTooltipEvent {

    protected RenderTooltipEventPost(ItemStack itemStack, GuiGraphics graphics, int x, int y, Font font, List<ClientTooltipComponent> components) {
        super(itemStack, graphics, x, y, font, components);
    };
  

};
