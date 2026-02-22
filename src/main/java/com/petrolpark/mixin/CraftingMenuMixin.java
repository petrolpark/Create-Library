package com.petrolpark.mixin;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.contamination.recipe.IHandleContaminationMyselfRecipe;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.recipe.book.IBookRequiredRecipe;
import com.petrolpark.core.recipe.book.RecipeBookItem;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Shadow
    private ContainerLevelAccess access;
    
    /**
     * Propagate Contaminants to the results of Crafted Items, if the configs say to do so.
     * Also begin Item Decay.
     * @param menu
     * @param level
     * @param player
     * @param craftSlots
     * @param resultSlots
     * @param recipe
     * @param ci
     * @param craftinginput
     * @param serverplayer
     * @param itemstack
     */
    @Inject(
        method = "Lnet/minecraft/world/inventory/CraftingMenu;slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;Lnet/minecraft/world/item/crafting/RecipeHolder;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void petrolpark$propagateContaminantsAndStartDecay(
        AbstractContainerMenu menu,
        Level level,
        Player player,
        CraftingContainer craftSlots,
        ResultContainer resultSlots,
        RecipeHolder recipe,
        CallbackInfo ci, CraftingInput craftinginput, ServerPlayer serverplayer, ItemStack itemstack
    ) {
        if (!itemstack.isEmpty()) {
            ItemDecay.startDecay(itemstack);
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, level, recipe); // For mystery reasons this cannot be localcaptured
            if (PetrolparkConfigs.server().craftingTablePropagatesContaminants.get() && optional.map(rh -> {
                if (rh.value() instanceof IHandleContaminationMyselfRecipe contamHandled) {
                    return !contamHandled.isContaminationHandled(craftinginput, level.registryAccess());
                } else return true;
            }).orElse(true)) {
                ItemContamination.perpetuateSingle(craftSlots.getItems().stream(), itemstack);
            };
        };
    };

    @ModifyExpressionValue(
        method = "Lnet/minecraft/world/inventory/CraftingMenu;slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;Lnet/minecraft/world/item/crafting/RecipeHolder;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor"
        )
    )
    private static Optional<RecipeHolder<CraftingRecipe>> petrolpark$addBookRequiredRecipes(Optional<RecipeHolder<CraftingRecipe>> original, AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots, @Nullable RecipeHolder<CraftingRecipe> lastRecipe) {
        ServerPlayer serverPlayer = (ServerPlayer)player;
        return original.or(() -> level.getRecipeManager()
            .getRecipeFor(PetrolparkRecipeTypes.CRAFTING_BOOK_REQUIRED.get(), craftSlots.asCraftInput(), level)
            .filter(rh -> 
                serverPlayer.getRecipeBook().contains(rh) // Player has used the Recipe Book to add the Recipe to their (Minecraft) Recipe Book
                || player.getInventory().hasAnyMatching(stack -> RecipeBookItem.streamProvidedRecipes(level, stack).anyMatch(rh::equals)) // Player is carrying the Recipe Book
                || menu instanceof CraftingMenu craftingMenu && craftingMenu.access.evaluate((l, pos) -> IBookRequiredRecipe.hasRequiredBook(l, pos, rh), false) // Crafting Table block is adjacent to Bookshelf supplying Recipe Book
            ).map(rh -> new RecipeHolder<>(rh.id(), (CraftingRecipe)rh.value()))
        );  
    };
};
