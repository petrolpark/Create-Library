package com.petrolpark.compat.jei.category;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.ContaminantInfoCategory.ContaminantInfoRecipe;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Pair;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.Internal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class ContaminantInfoCategory<STACK> extends AbstractRecipeCategory<ContaminantInfoRecipe<STACK>> {

    @SuppressWarnings("unchecked")
    protected static final Class<ContaminantInfoRecipe<ItemStack>> ITEM_INFO_RECIPE_CLASS = (Class<ContaminantInfoRecipe<ItemStack>>)(Class<?>)ContaminantInfoRecipe.class;
    public static final mezz.jei.api.recipe.RecipeType<ContaminantInfoRecipe<ItemStack>> ITEM_RECIPE_TYPE = mezz.jei.api.recipe.RecipeType.create(Petrolpark.MOD_ID, "item_contaminants", ITEM_INFO_RECIPE_CLASS);

    final IIngredientType<STACK> ingredientType;

    public ContaminantInfoCategory(IGuiHelper guiHelper, IIngredientType<STACK> ingredientType, mezz.jei.api.recipe.RecipeType<ContaminantInfoRecipe<STACK>> recipeType) {
        super(recipeType, Lang.translate("recipe.contamination"), Internal.getTextures().getInfoIcon(), 170, 125);
        this.ingredientType = ingredientType;
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ContaminantInfoRecipe<STACK> recipe, IFocusGroup focuses) {
        if (recipe.stack().isPresent()) builder.addInputSlot(0, 0)
            .addIngredient(ingredientType, recipe.stack().get().getFirst())
            .setStandardSlotBackground();
    };

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ContaminantInfoRecipe<STACK> recipe, IFocusGroup focuses) {
        //TODO make these better
        builder.addScrollBoxWidget(164, 27, 3, 45)
            .setContents(recipe.contaminant().value().getChildren().stream().<FormattedText>map(Contaminant::getNameColored).toList());
        builder.addScrollBoxWidget(164, 27, 3, 90)
            .setContents(recipe.contaminant().value().getParents().stream().<FormattedText>map(Contaminant::getNameColored).toList());
    };

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ContaminantInfoRecipe<STACK> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final Component name = Contaminant.getNameColored(recipe.contaminant());
        if (mouseY < 18) { // Hovering the title
            if (recipe.stack().isPresent()) tooltip.add(translate(recipe.stack().get().getSecond().isIntrinsic(recipe.contaminant()) ? "intrinsic" : "extrinsic", name));
        } else if (mouseY < 30) { // Hovering "preservation proportion"
            final float preservationProportion = recipe.contaminant().value().getPreservationProportion();
            if (preservationProportion == 0f) {
                tooltip.add(translate("preservationProportion.any", name));
            } else if (preservationProportion == 1f) {
                tooltip.add(translate("preservationProportion.all", name));
            } else {
                tooltip.add(translate("preservationProportion.some", Lang.ONE_DP_DF.format(preservationProportion * 100f), name));
            };
            tooltip.add(translate("uncontaminated", Contaminant.getAbsentNameColored(recipe.contaminant())));
            tooltip.add(translate("uncontaminableIgnored"));
        } else if (mouseY < 75) { // Hovering children
            tooltip.add(translate("children.explanation", name));
        } else { // Hovering parents
            tooltip.add(translate("parents.explanation", name));
        };
    };

    @Override
    public void draw(ContaminantInfoRecipe<STACK> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, Contaminant.getNameColored(recipe.contaminant()), recipe.stack().isPresent() ? 20 : 0, 3, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("preservationProportion", Lang.ONE_DP_DF.format(recipe.contaminant().value().getPreservationProportion() * 100)), 0, 20, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("children"), 0, 32, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("parents"), 0, 77, 0xFFFFFF);
        guiGraphics.fill(0, 42, 153, 75, 0xFF8B8B8B);
        guiGraphics.fill(0, 87, 153, 120, 0xFF8B8B8B);
    };

    protected final Component translate(String suffix, Object ... args) {
        return Lang.translate("jei.contamination." + suffix, args);
    };

    public record ContaminantInfoRecipe<STACK>(Optional<Pair<STACK, IContamination<?, STACK>>> stack, Holder<Contaminant> contaminant) {

        public static ContaminantInfoRecipe<ItemStack> forItemStacks(Holder<Contaminant> contaminant) {
            return new ContaminantInfoRecipe<>(Optional.empty(), contaminant);
        };
    };
};
