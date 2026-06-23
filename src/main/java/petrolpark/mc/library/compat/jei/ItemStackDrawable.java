package petrolpark.mc.library.compat.jei;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.ItemIcon;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Copied from {@link ItemIcon Create source code}
 */
public class ItemStackDrawable implements IDrawable {

	private Supplier<ItemStack> supplier;
	private ItemStack stack;

	public ItemStackDrawable(Supplier<ItemStack> stack) {
		this.supplier = stack;
	};

	@Override
	public int getWidth() {
		return 18;
	};

	@Override
	public int getHeight() {
		return 18;
	};

	@Override
	public void draw(@Nonnull GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
		if (stack == null) {
			stack = supplier.get();
		};

		RenderSystem.enableDepthTest();
		matrixStack.pushPose();
		matrixStack.translate(xOffset + 1, yOffset + 1, 0);

		graphics.renderFakeItem(stack, 0, 0);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, 0, 0);

		matrixStack.popPose();
	};


}

