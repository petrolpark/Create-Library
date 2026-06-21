package petrolpark.mc.library.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

@Mixin(RecipeApplier.class)
public class RecipeApplierMixin {

    @ModifyReturnValue(
        method = "Lcom/simibubi/create/foundation/recipe/RecipeApplier;applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;",
        at = @At("RETURN"),
        remap = false
    )
    private static List<ItemStack> petrolpark$propagateFlagsAndStartDecay(List<ItemStack> original, Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder) {
        if (PetrolparkConfigs.server().createOtherRecipesPropagateFlags.get()) {
            IFlagPole<?, ?> inputFlags = ItemFlagPole.get(stackIn);
            original.stream().map(ItemFlagPole::get).forEach(c -> c.flagAll(inputFlags.streamAllFlags()));
        };
        original.forEach(ItemDecay::startDecay);
        return original;
    };
};
