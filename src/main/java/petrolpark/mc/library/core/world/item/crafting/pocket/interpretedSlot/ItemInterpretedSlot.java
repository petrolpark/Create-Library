package petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.Petrolpark;

public record ItemInterpretedSlot(Slot slot) implements IInterpretedSlot<ItemStack> {

    public static final String TRANSLATION_KEY = Petrolpark.translationKey("pocketCrafting.slotInterpretation.item");

    public ItemStack ingredient() {
        return slot().getItem();
    };

    @Override
    public Class<ItemStack> ingredientClass() {
        return ItemStack.class;
    };

    @Override
    public Component name() {
        return Component.translatable(TRANSLATION_KEY);
    };
};
