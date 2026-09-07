package petrolpark.mc.library.mixin.compat.farmersdelight;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.util.ItemHelper;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotRecipe.class)
public class CookingPotRecipeMixin {
    
    @ModifyReturnValue(
        method = "assemble",
        at = @At("RETURN")
    )
    public ItemStack petrolpark$propagateFlags(ItemStack original, RecipeWrapper inv, HolderLookup.Provider provider) {
        if (!PetrolparkConfigs.server().farmersDelightCookingRecipesPropagateFlags.get()) return original;
        ItemFlagPole.perpetuate(ItemHelper.stream(inv), Stream.of(original));
        return original;
    };
};
