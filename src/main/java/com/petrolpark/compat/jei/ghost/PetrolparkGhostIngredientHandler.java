package com.petrolpark.compat.jei.ghost;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.petrolpark.RequiresCreate;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Mainly copied from {@link com.simibubi.create.compat.jei.GhostIngredientHandler Create source code}.
 */
@RequiresCreate
public class PetrolparkGhostIngredientHandler<MENU extends GhostItemMenu<?>, SCREEN extends AbstractSimiContainerScreen<? extends MENU>> implements IGhostIngredientHandler<SCREEN> {

	@Override
	public <I> List<IGhostIngredientHandler.Target<I>> getTargetsTyped(@Nonnull SCREEN gui, @Nonnull ITypedIngredient<I> ingredient, boolean doStart) {
		final List<IGhostIngredientHandler.Target<I>> targets = new LinkedList<>();
		
		if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
			for (int i = 36; i < gui.getMenu().slots.size(); i++) {
				final Slot slot = gui.getMenu().slots.get(i);
				if (slot.isActive() && !(slot instanceof IConditionalGhostSlot ghostSlot && !ghostSlot.canSetGhostItem())) targets.add(new PetrolparkGhostTarget<>(gui, i - 36));
			};
		};
		
		return targets;
	};

	@Override
	public void onComplete() {}

	@Override
	public boolean shouldHighlightTargets() {
		return true;
	};

	public static class PetrolparkGhostTarget<I, MENU extends GhostItemMenu<?>, SCREEN extends AbstractSimiContainerScreen<? extends MENU>> implements IGhostIngredientHandler.Target<I> {

		private final Rect2i area;
		private final SCREEN screen;
		private final int slotIndex;

		public PetrolparkGhostTarget(SCREEN screen, int slotIndex) {
			this.screen = screen;
			this.slotIndex = slotIndex;
			final Slot slot = screen.getMenu().slots.get(slotIndex + 36);
			this.area = new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);
		};

		@Override
		public Rect2i getArea() {
			return area;
		};

		@Override
		public void accept(I ingredient) {
			ItemStack stack = ((ItemStack) ingredient).copy();
			stack.setCount(1);
			screen.getMenu().ghostInventory.setStackInSlot(slotIndex, stack);
			CatnipServices.NETWORK.sendToServer(new GhostItemSubmitPacket(stack, slotIndex));
		};
	};
    
};
