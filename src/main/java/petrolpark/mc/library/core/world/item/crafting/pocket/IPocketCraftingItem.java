package petrolpark.mc.library.core.world.item.crafting.pocket;

import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;

public interface IPocketCraftingItem {
    
    public IPocketCrafter<?> getPocketCrafter(ItemStack stack);
};
