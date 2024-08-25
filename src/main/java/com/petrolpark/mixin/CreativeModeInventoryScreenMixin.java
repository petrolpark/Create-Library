package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.client.creativemodetab.CustomTab;
import com.petrolpark.client.creativemodetab.CustomTab.ITabEntry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> {
    
    public CreativeModeInventoryScreenMixin(ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        throw new AssertionError(); // Should never be called
    };

    /**
     * Render fancy things in {@link CustomTab Custom Tabs}.
     */
    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("RETURN")
    )
    public void inRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (getSelectedTab() instanceof CustomTab tab) {
            int offset = getMenu().getRowIndexForScroll(getScrollOffs());
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(getGuiLeft() + 8f, getGuiTop() + 17f, 0f);
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 9; x++) {
                    ITabEntry entry = tab.renderedEntries.get(9 * (y + offset) + x);
                    if (entry != null) {
                        ms.pushPose();
                        ms.translate(x * 18f, y * 18f, 0f);
                        entry.render(graphics, mouseX, mouseY, partialTick);
                        ms.popPose();
                    };
                };
            };
            ms.popPose();
        };
    };

    @Accessor("selectedTab")
    public static CreativeModeTab getSelectedTab() {
        throw new AssertionError();
    };

    @Accessor("scrollOffs")
    public abstract float getScrollOffs();
};
