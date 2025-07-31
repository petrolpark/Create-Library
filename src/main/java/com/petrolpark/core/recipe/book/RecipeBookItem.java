package com.petrolpark.core.recipe.book;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.compat.jei.PetrolparkJEI;
import com.simibubi.create.compat.Mods;

import mezz.jei.gui.overlay.bookmarks.PreviewTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class RecipeBookItem extends Item {

    public static Stream<RecipeHolder<?>> streamProvidedRecipes(Level level, ItemStack stack) {
        Stream<RecipeHolder<?>> recipes;
        List<ResourceLocation> recipesComponent = stack.get(DataComponents.RECIPES);
        if (recipesComponent != null) recipes = recipesComponent.stream().flatMap(rl -> level.getRecipeManager().byKey(rl).stream()); else recipes = Stream.empty();
        RecipeReferenceDataComponent recipeReferenceComponent = stack.get(PetrolparkDataComponents.RECIPE_REFERENCE);
        if (recipeReferenceComponent != null) recipes = Stream.concat(recipes, recipeReferenceComponent.getRecipeHolder(level.getRecipeManager()).stream());
        return recipes;
    };

    public RecipeBookItem(Properties properties) {
        super(properties);
    };

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        RecipeReferenceDataComponent recipeReference = stack.get(PetrolparkDataComponents.RECIPE_REFERENCE);
        Level level = context.level();
        if (level != null && recipeReference != null) {
            if (recipeReference.jeiRecipeTypeId().isPresent() && Mods.JEI.isLoaded()) return;
            tooltipComponents.add(recipeReference.getRecipeName(level.getRecipeManager()).copy().withStyle(ChatFormatting.GRAY));
        };
    };

    @Override
    public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        return Mods.JEI.runIfInstalled(() -> () -> getJeiTooltipImage(stack)).orElse(Optional.empty());
    };

    public Optional<TooltipComponent> getJeiTooltipImage(@Nonnull ItemStack stack) {
        return Petrolpark.runForDist(() -> () -> {
            Minecraft mc = Minecraft.getInstance();
            ClientPacketListener connection = mc.getConnection();
            RecipeReferenceDataComponent recipeReference = stack.get(PetrolparkDataComponents.RECIPE_REFERENCE);
            if (connection == null || recipeReference == null || PetrolparkJEI.JEI_RUNTIME == null) return Optional.empty();

            return PetrolparkJEI.RECIPE_BOOK_ITEM_JEI_CATEGORY_CACHE.get(recipeReference, connection.getRecipeManager())
                .map(layout -> new PreviewTooltipComponent<>(layout));
        }, () -> Optional::empty);
    };


    
};
