package com.petrolpark.client.rendering;

import com.simibubi.create.foundation.gui.element.ScreenElement;

import net.minecraft.resources.ResourceLocation;

public interface IGuiTexture extends ScreenElement {

    public ResourceLocation getLocation();
    
    public int getStartX();

    public int getStartY();

    public int getWidth();

    public int getHeight();

    public int getTextureWidth();

    public int getTextureHeight();

    public void bind();
};
