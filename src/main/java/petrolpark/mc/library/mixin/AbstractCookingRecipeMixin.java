package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.ItemFlagPole;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

@Mixin(AbstractCookingRecipe.class)
public class AbstractCookingRecipeMixin {
  
    @WrapMethod(
        method = "Lnet/minecraft/world/item/crafting/AbstractCookingRecipe;assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"
    )
    public ItemStack petrolpark$propagateFlags(SingleRecipeInput input, HolderLookup.Provider registries, Operation<ItemStack> original) {
        final ItemStack result = original.call(input, registries);
        boolean propagate;
        try {
            propagate = PetrolparkConfigs.server().cookingPropagatesFlags.get(); 
        } catch (IllegalStateException e) {
            propagate = false;
        };
        if (propagate) ItemFlagPole.get(result).flagAll(ItemFlagPole.get(input.item()).streamAllFlags());
        return result;
    };
};
