package com.petrolpark.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum PetrolparkGuiTexture implements IGuiTexture {

    // JEI
	JEI_POINTING_HAND("jei/widgets", 40, 40, 18, 14),
    JEI_SHORT_DOWN_ARROW("jei/widgets", 0, 64, 18, 18),
    JEI_SHORT_RIGHT_ARROW("jei/widgets", 0, 82, 18, 16),
	JEI_EQUILIBRIUM_ARROW("jei/widgets", 0, 96, 42, 11),
	JEI_LINE("jei/widgets", 40, 38, 177, 2),
	JEI_TEXT_BOX_LONG("jei/widgets",169, 19),
	JEI_TEXT_BOX_SHORT("jei/widgets", 0, 19, 115, 19),
	JEI_DISTILLATION_TOWER_BOTTOM("jei/widgets", 0, 52, 12, 12),
	JEI_DISTILLATION_TOWER_MIDDLE("jei/widgets", 0, 40, 20, 12),
	JEI_DISTILLATION_TOWER_TOP("jei/widgets", 0, 38, 12, 2),
	JEI_DISTILLATION_TOWER_BRANCH("jei/widgets", 20, 45, 20, 2),
	JEI_EXPLOSION("jei/widgets", 169, 0, 18, 21),
    JEI_NERD_EMOJI("jei/widgets", 115, 19, 16, 14),
    JEI_GLOBE("jei/widgets", 115 + 16, 19, 16, 14),
    
    // Creative Mode Tab
	CREATIVE_MODE_TAB_BLANK_ROW("creative_inventory", 0, 0, 162, 18),
    ;

    public final ResourceLocation location;
	public final int width, height, startX, startY, textureWidth, textureHeight;

	private PetrolparkGuiTexture(String location, int width, int height) {
		this(location, 0, 0, width, height);	
	};

	private PetrolparkGuiTexture(String location, int startX, int startY, int width, int height) {
		this(location, startX, startY, width, height, 256, 256);
	};

    private PetrolparkGuiTexture(String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
		this.location = Petrolpark.asResource("textures/gui/" + location + ".png");
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	};

    @OnlyIn(Dist.CLIENT)
    @Override
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height, textureWidth, textureHeight);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, 1, startX, startY, width, height, textureWidth, textureHeight);
	};

    @Override
    public ResourceLocation getLocation() {
        return location;
    };

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    };

    @Override
    public int getWidth() {
        return width;
    };

    @Override
    public int getHeight() {
        return height;
    };

    @Override
    public int getTextureWidth() {
        return textureWidth;
    };

    @Override
    public int getTextureHeight() {
        return textureHeight;
    };
};
