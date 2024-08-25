package com.petrolpark.client.rendering.item.decorator;

import com.petrolpark.Petrolpark;
import com.petrolpark.itemdecay.IDecayingItem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public class DecayingItemDecorator implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!Screen.hasShiftDown()) return false;
        CompoundTag tag = stack.getOrCreateTag();
        if (!(stack.getItem() instanceof IDecayingItem item)) return false;
        if (!tag.contains("CreationTime", Tag.TAG_LONG)) return false;
        float proportion = 1f + (float)(tag.getLong("CreationTime") - Petrolpark.DECAYING_ITEM_HANDLER.get().getGameTime()) / (float)item.getLifetime(stack);
        if (proportion <= 0f) return false;
        int color = Mth.hsvToRgb(proportion / 3f, 0.5f + proportion * 0.5f, 0.25f + proportion * 0.75f);
        guiGraphics.fill(RenderType.guiOverlay(), xOffset + 2, yOffset + 3, xOffset + 14, yOffset + 5, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), xOffset + 2, yOffset + 3, xOffset + 2 + (int)(proportion * 12f), yOffset + 4, color | 0xFF000000);
        return false;
    };
    
};
