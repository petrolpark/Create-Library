package petrolpark.mc.library.core.data.loot.wish;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class ClientWishToastHelper {
    public static void tryShowToast(IAdvancedIngredient<ItemStack> wish, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getToasts().getToast(WishGrantedToast.class, wish) == null)
            mc.getToasts().addToast(new WishGrantedToast(wish, stack));
    }
}
