package com.petrolpark.compat.jei.widget;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.library.gui.widgets.ScrollGridRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Modification of {@link ScrollGridRecipeWidget} to allow custom slot backgrounds
 */
public class CustomScrollGridRecipeWidget extends ScrollGridRecipeWidget {

    // Override
    protected final IDrawable slotBackground;
    private final int columns;
	private final int visibleRows;
	private final int hiddenRows;
	private final List<IRecipeSlotDrawable> slots;

    public CustomScrollGridRecipeWidget(int columns, int visibleRows, List<IRecipeSlotDrawable> slots, IDrawable slotBackground) {
        super(new ImmutableRect2i(0, 0, columns * slotBackground.getWidth() + getScrollBoxScrollbarExtraWidth(), visibleRows * slotBackground.getHeight()), columns, visibleRows, slots);
        this.slotBackground = slotBackground;
        this.columns = columns;
        this.visibleRows = visibleRows;
		this.hiddenRows = Math.max(MathUtil.divideCeil(slots.size(), columns) - visibleRows, 0);
        this.slots = slots;
    };

    @Override
	protected void drawContents(@Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY, float scrollOffsetY) {
		final int totalSlots = slots.size();
		final int firstRow = getRowIndexForScroll(hiddenRows, getScrollOffsetY());
		final int firstIndex = columns * firstRow;

		final int slotWidth = slotBackground.getWidth();
		final int slotHeight = slotBackground.getHeight();

		for (int row = 0; row < visibleRows; row++) {
			final int y = row * slotHeight;
			for (int column = 0; column < columns; column++) {
				final int x = column * slotWidth;
				final int slotIndex = firstIndex + (row * columns) + column;
				slotBackground.draw(guiGraphics, x, y);
				if (slotIndex < totalSlots) {
					IRecipeSlotDrawable slot = slots.get(slotIndex);
					slot.setPosition(x + 1, y + 1);
					slot.draw(guiGraphics);
				};
			};
		};
	};

    private int getRowIndexForScroll(int hiddenRows, float scrollOffset) {
		return Math.max((int) ((double) (scrollOffset * (float) hiddenRows) + 0.5d), 0);
	};
    
};
