package com.petrolpark.core.data.loot.wish;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class ClientWishToastHelper {
    public static void tryShowToast(IAdvancedIngredient<? super ItemStack> wish, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getToasts().getToast(WishGrantedToast.class, wish) == null)
            mc.getToasts().addToast(new WishGrantedToast(wish, stack));
    }
}
