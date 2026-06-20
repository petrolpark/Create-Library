package com.petrolpark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.petrolpark.core.world.item.crafting.recipeBook.IBookRequiredRecipe;
import com.petrolpark.registry.PetrolparkRecipeTypes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(CrafterMenu.class)
public class CrafterMenuMixin {

    @Shadow
    private CraftingContainer container;

    @Shadow
    private Player player;
    
    @ModifyExpressionValue(
        method = "refreshRecipeResult",
        at = @At(
            value = "INVOKE",
            target = "getPotentialResults"
        )
    )
    public Optional<RecipeHolder<CraftingRecipe>> petrolpark$addBookRequiredRecipes(Optional<RecipeHolder<CraftingRecipe>> original) {
        if (!(container instanceof BlockEntity be)) return original;
        return original.or(() -> player.level().getRecipeManager()
            .getRecipeFor(PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get(), container.asCraftInput(), player.level())
            .filter(rh -> IBookRequiredRecipe.hasRequiredBook(be, rh))
            .map(rh -> new RecipeHolder<>(rh.id(), (CraftingRecipe)rh.value()))
        );
    };
};
