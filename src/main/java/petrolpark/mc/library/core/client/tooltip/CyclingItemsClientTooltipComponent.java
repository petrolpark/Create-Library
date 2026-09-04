package petrolpark.mc.library.core.client.tooltip;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(Dist.CLIENT)
@ParametersAreNonnullByDefault
public record CyclingItemsClientTooltipComponent(List<ItemStack> stacks) implements ClientTooltipComponent {

    public CyclingItemsClientTooltipComponent(CyclingItemsClientTooltipComponent.Image tooltipComponent) {
        this(tooltipComponent.stacks());
    };

    @Override
    public int getHeight() {
        return 16;
    };

    @Override
    public int getWidth(Font font) {
        return 16;
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (stacks().isEmpty()) return;
        final int seconds = AnimationTickHolder.getTicks(true) / 20;
        guiGraphics.renderFakeItem(stacks().get(seconds % stacks().size()), x, y);
    };

    @SubscribeEvent
    public static final void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CyclingItemsClientTooltipComponent.Image.class, CyclingItemsClientTooltipComponent::new);
    };

    public record Image(List<ItemStack> stacks) implements TooltipComponent {};
    
};
