package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.core.flags.recipe.IHandleFlagsMyselfRecipe;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Allow Shapeless Recipes to propagate the Flags of the Ingredients to the result.
 */
@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements IHandleFlagsMyselfRecipe<CraftingInput> {

    @ModifyReturnValue(
        method = "Lnet/minecraft/world/item/crafting/ShapelessRecipe;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN")
    )
    public ItemStack petrolpark$propagateFlagsAndStartDecay(ItemStack original, CraftingInput input, HolderLookup.Provider registries) {
        ItemDecay.startDecay(original);
        if (PetrolparkConfigs.server().shapelessCraftingPropagatesFlags.get()) ItemFlagPole.perpetuateSingle(input.items().stream(), original);
        return original;
    };

    @Override
    public boolean isFlagsHandled(CraftingInput input, HolderLookup.Provider registrie) {
        return PetrolparkConfigs.server().shapelessCraftingPropagatesFlags.get();
    };
    

};
