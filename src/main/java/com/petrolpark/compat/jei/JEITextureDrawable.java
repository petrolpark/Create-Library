package com.petrolpark.compat.jei;

import com.petrolpark.client.rendering.IGuiTexture;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class JEITextureDrawable implements IDrawable {

    private final IGuiTexture texture;

    private JEITextureDrawable(IGuiTexture texture) {
        this.texture = texture;
    };

    public static JEITextureDrawable of(IGuiTexture texture) {
        return new JEITextureDrawable(texture);
    };

    @Override
    public int getWidth() {
        return texture.getWidth();
    };

    @Override
    public int getHeight() {
        return texture.getHeight();
    };

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        texture.render(guiGraphics, xOffset, yOffset);
    };
    
};
