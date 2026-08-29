package petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;

import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.RequiresCreate;

@RequiresCreate
public class ExtractedFluidInterpretedSlot implements IInterpretedSlot<FluidStack> {

    public static final String TRANSLATION_KEY = Petrolpark.translationKey("pocketCrafting.slotInterpretation.fluid");

    protected final Slot slot;
    protected final FluidStack fluidStack;
    protected final ItemStack emptiedItemStack;

    public ExtractedFluidInterpretedSlot(Level level, Slot slot) {
        this.slot = slot;
        final Pair<FluidStack, ItemStack> emptied = GenericItemEmptying.emptyItem(level, slot.getItem(), true);
        this.fluidStack = emptied.getFirst();
        this.emptiedItemStack = emptied.getSecond();
    };

    @Override
    public Slot slot() {
        return slot;
    };

    @Override
    public FluidStack ingredient() {
        return fluidStack;
    };

    @Override
    public Class<FluidStack> ingredientClass() {
        return FluidStack.class;
    };

    @Override
    public Component name() {
        return Component.translatable(TRANSLATION_KEY);
    };

    @Override
    public boolean render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        return IInterpretedSlot.super.render(graphics);
    };
    
};
