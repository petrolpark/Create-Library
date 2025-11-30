package com.petrolpark.compat.create;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.gui.AllIcons;

import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Copied from {@link AllIcons Create source code}.
 */
public class PetrolparkIcon extends AllIcons {

    public static final ResourceLocation ICON_ATLAS = Petrolpark.asResource("textures/gui/icons.png");

    private static int x = 0, y = -1;
    private final int iconX, iconY;

    public PetrolparkIcon(int x, int y) {
        super(x, y);
        iconX = x * 16;
        iconY = y * 16;
    };

    public static final PetrolparkIcon

    REDSTONE_PROGRAMMER_MODE_MANUAL = newRow(),
	REDSTONE_PROGRAMMER_MODE_SWITCH_ON_PULSE = next(),
	REDSTONE_PROGRAMMER_MODE_RESTART_ON_PULSE = next(),
	REDSTONE_PROGRAMMER_MODE_RESUME_WITH_POWER = next(),
	REDSTONE_PROGRAMMER_MODE_RESTART_WITH_POWER = next(),
	REDSTONE_PROGRAMMER_MODE_LOOP_WITH_POWER = next(),
	REDSTONE_PROGRAMMER_MODE_LOOP = next();

    private static PetrolparkIcon next() {
		return new PetrolparkIcon(++x, y);
	};

	private static PetrolparkIcon newRow() {
		return new PetrolparkIcon(x = 0, ++y);
	};

    protected ResourceLocation getTextureLocation() {
        return ICON_ATLAS;
    };

    @OnlyIn(Dist.CLIENT)
    @Override
	public void bind() {
		RenderSystem.setShaderTexture(0, getTextureLocation());
	};

	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(getTextureLocation(), x, y, 0, iconX, iconY, 16, 16, 256, 256);
	};

	@OnlyIn(Dist.CLIENT)
    @Override
	public void render(PoseStack ms, MultiBufferSource buffer, int color) {
		VertexConsumer builder = buffer.getBuffer(RenderType.text(getTextureLocation()));
		Matrix4f matrix = ms.last().pose();
		Color rgb = new Color(color);
		int light = LightTexture.FULL_BRIGHT;

		Vec3 vec1 = new Vec3(0, 0, 0);
		Vec3 vec2 = new Vec3(0, 1, 0);
		Vec3 vec3 = new Vec3(1, 1, 0);
		Vec3 vec4 = new Vec3(1, 0, 0);

		float u1 = iconX * 1f / ICON_ATLAS_SIZE;
		float u2 = (iconX + 16) * 1f / ICON_ATLAS_SIZE;
		float v1 = iconY * 1f / ICON_ATLAS_SIZE;
		float v2 = (iconY + 16) * 1f / ICON_ATLAS_SIZE;

		vertex(builder, matrix, vec1, rgb, u1, v1, light);
		vertex(builder, matrix, vec2, rgb, u1, v2, light);
		vertex(builder, matrix, vec3, rgb, u2, v2, light);
		vertex(builder, matrix, vec4, rgb, u2, v1, light);
	};

	@OnlyIn(Dist.CLIENT)
	private void vertex(VertexConsumer builder, Matrix4f matrix, Vec3 vec, Color rgb, float u, float v, int light) {
		builder.addVertex(matrix, (float) vec.x, (float) vec.y, (float) vec.z)
			.setColor(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 255)
			.setUv(u, v)
			.setLight(light);
	};
    
};
