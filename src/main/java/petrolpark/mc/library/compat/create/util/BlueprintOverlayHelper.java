package petrolpark.mc.library.compat.create.util;

import java.util.List;

import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.mixin.compat.create.accessor.client.BlueprintOverlayRendererAccessor;
import petrolpark.mc.library.util.BigItemStack;

public class BlueprintOverlayHelper {
  
    public static final void displayRequiredItems(Item required, int count, boolean fulfilled) {
        if (BlueprintOverlayRendererAccessor.isActive()) return;
        BlueprintOverlayRendererAccessor.invokePrepareCustomOverlay();

        while (count > 0) {
            BlueprintOverlayRendererAccessor.getIngredients().add(Pair.of(new ItemStack(required, Math.min(64, count)), fulfilled));
            count -= 64;
        };
    };

    public static final void displayRequiredItems(List<BigItemStack> stacks, boolean fulfilled) {
        if (BlueprintOverlayRendererAccessor.isActive()) return;
        BlueprintOverlayRendererAccessor.invokePrepareCustomOverlay();
        BlueprintOverlayRendererAccessor.getIngredients().addAll(stacks.stream().map(BigItemStack::getAsStacks).flatMap(List::stream).map(stack -> Pair.of(stack, fulfilled)).toList());
    };
};
