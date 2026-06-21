package petrolpark.mc.library.core.world.item.decay;

import javax.annotation.Nonnull;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Petrolpark.MOD_ID)
public class DecayingItemDecorator implements IItemDecorator {

    @Override
    public boolean render(@Nonnull GuiGraphics guiGraphics, @Nonnull Font font, @Nonnull ItemStack stack, int xOffset, int yOffset) {
        if (!Screen.hasShiftDown()) return false;
        if (!stack.has(PetrolparkDataComponentTypes.DECAY_TIME)) return false;
        Long creationTime = stack.get(PetrolparkDataComponentTypes.DECAY_START_TIME);
        if (creationTime == null) return false;
        float proportion = 1f + (float)(creationTime - ItemDecay.getGameTime()) / (float)ItemDecay.getLifetimeOrNone(stack);
        proportion = Mth.clamp(proportion, 0f, 1f);
        int color = Mth.hsvToRgb(proportion / 3f, 0.5f + proportion * 0.5f, 0.25f + proportion * 0.75f);
        guiGraphics.fill(RenderType.guiOverlay(), xOffset + 2, yOffset + 3, xOffset + 14, yOffset + 5, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), xOffset + 2, yOffset + 3, xOffset + 2 + (int)(proportion * 12f), yOffset + 4, color | 0xFF000000);
        return false;
    };

    @SubscribeEvent
    public static final void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        final DecayingItemDecorator decorator = new DecayingItemDecorator();
        BuiltInRegistries.ITEM.forEach(item -> event.register(item, decorator));
    };
    
};
