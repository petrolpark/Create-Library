package petrolpark.mc.library.core.world.item.crafting.pocket;

import net.minecraft.world.item.ItemStack;

public record ItemPocketCraftingResult(ItemStack stack, boolean successful) implements PocketCrafting.Result {
    
    public static ItemPocketCraftingResult success(ItemStack stack) {
        return new ItemPocketCraftingResult(stack, true);
    };

    public static ItemPocketCraftingResult fail(ItemStack stack) {
        return new ItemPocketCraftingResult(stack, false);
    };
};
