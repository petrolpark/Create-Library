package petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

public interface IInterpretedSlot<T> {

    public Slot slot();

    public T ingredient();

    public Class<T> ingredientClass();

    public Component name();

    public default boolean render(GuiGraphics graphics) {
        return false;
    };
};
