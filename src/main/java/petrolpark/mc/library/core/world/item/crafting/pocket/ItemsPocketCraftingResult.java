package petrolpark.mc.library.core.world.item.crafting.pocket;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public record ItemsPocketCraftingResult(List<ItemsPocketCraftingResult.Output> outputs, boolean successful) implements PocketCrafting.Result {
    
    public static ItemsPocketCraftingResult success(ItemStack stack) {
        return new ItemsPocketCraftingResult(Collections.singletonList(new ItemsPocketCraftingResult.Output(stack)), true);
    };

    public static ItemsPocketCraftingResult success(Stream<ItemStack> stacks) {
        return new ItemsPocketCraftingResult(stacks.map(ItemsPocketCraftingResult.Output::new).toList(), true);
    };

    public static ItemsPocketCraftingResult fail(Stream<ItemStack> stacks) {
        return new ItemsPocketCraftingResult(stacks.map(ItemsPocketCraftingResult.Output::new).toList(), false);
    };

    public record Output(ItemStack stack) implements PocketCrafting.Output {

        @Override
        public void render(GuiGraphics graphics) {
            graphics.renderItem(stack(), 0, 0);
            graphics.renderItemDecorations(Minecraft.getInstance().font, stack(), 0, 0);
        };

    };
};
