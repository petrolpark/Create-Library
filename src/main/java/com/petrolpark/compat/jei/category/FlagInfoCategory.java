package com.petrolpark.compat.jei.category;

import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.FlagInfoCategory.FlagInfoRecipe;
import com.petrolpark.compat.jei.ingredient.FlagIngredientType;
import com.petrolpark.compat.jei.widget.CustomScrollGridRecipeWidget;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Pair;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
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
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class FlagInfoCategory<STACK> extends AbstractRecipeCategory<FlagInfoRecipe<STACK>> {

    @SuppressWarnings("unchecked")
    protected static final Class<FlagInfoRecipe<ItemStack>> ITEM_INFO_RECIPE_CLASS = (Class<FlagInfoRecipe<ItemStack>>)(Class<?>)FlagInfoRecipe.class;
    public static final mezz.jei.api.recipe.RecipeType<FlagInfoRecipe<ItemStack>> ITEM_RECIPE_TYPE = mezz.jei.api.recipe.RecipeType.create(Petrolpark.MOD_ID, "item_flags", ITEM_INFO_RECIPE_CLASS);

    final IIngredientType<STACK> ingredientType;

    public FlagInfoCategory(IGuiHelper guiHelper, IIngredientType<STACK> ingredientType, mezz.jei.api.recipe.RecipeType<FlagInfoRecipe<STACK>> recipeType) {
        super(recipeType, Lang.translate("recipe.flags"), Internal.getTextures().getInfoIcon(), 170, 125);
        this.ingredientType = ingredientType;
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FlagInfoRecipe<STACK> recipe, IFocusGroup focuses) {
        
        // Optional "input": Item stack
        if (recipe.stack().isPresent()) builder.addInputSlot(0, 0)
            .addIngredient(ingredientType, recipe.stack().get().getFirst())
            .setStandardSlotBackground();

        // Hidden "output": Flag
        builder.addOutputSlot()
            .addIngredient(FlagIngredientType.TYPE, recipe.flag().value());

        // Output: children
        int children = 0;
        for (Holder<Flag> child : recipe.flag().value().getChildren()) {
            builder.addOutputSlot()
                .addIngredient(FlagIngredientType.TYPE, child.value())
                .setCustomRenderer(FlagIngredientType.TYPE, FlagIngredientType.FULL_RENDERER)
                .setSlotName("child_" + children++);
        };
    };

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FlagInfoRecipe<STACK> recipe, IFocusGroup focuses) {
        //TODO make these better
        // builder.addScrollBoxWidget(164, 27, 3, 45)
        //     .setContents(recipe.flag().value().getChildren().stream().<FormattedText>map(Flag::getNameColored).toList());
        // builder.addScrollBoxWidget(164, 27, 3, 90)
        //     .setContents(recipe.flag().value().getParents().stream().<FormattedText>map(Flag::getNameColored).toList());
    
        final List<IRecipeSlotDrawable> childrenSlots = builder.getRecipeSlots().getSlots().stream().filter(slot -> slot.getSlotName().map(name -> name.startsWith("child")).orElse(false)).toList();

        final CustomScrollGridRecipeWidget widget = new CustomScrollGridRecipeWidget(1, 3, childrenSlots, new IDrawable() {

            @Override
            public int getWidth() {
                return 164;
            };

            @Override
            public int getHeight() {
                return 10;
            };

            @Override
            public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
                
            };
                
        });
        widget.setPosition(3, 45);

        builder.addSlottedWidget(widget, childrenSlots);
        builder.addInputHandler(widget);
    };

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FlagInfoRecipe<STACK> recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final Component name = Flag.getNameColored(recipe.flag());
        if (mouseY < 18) { // Hovering the title
            if (recipe.stack().isPresent()) tooltip.add(translate(recipe.stack().get().getSecond().isIntrinsic(recipe.flag()) ? "intrinsic" : "extrinsic", name));
        } else if (mouseY < 30) { // Hovering "preservation proportion"
            final float preservationProportion = recipe.flag().value().getPreservationProportion();
            if (preservationProportion == 0f) {
                tooltip.add(translate("preservationProportion.any", name));
            } else if (preservationProportion == 1f) {
                tooltip.add(translate("preservationProportion.all", name));
            } else {
                tooltip.add(translate("preservationProportion.some", Lang.ONE_DP_DF.format(preservationProportion * 100f), name));
            };
            tooltip.add(translate("unflagged", Flag.getAbsentNameColored(recipe.flag())));
            tooltip.add(translate("unflaggableIgnored"));
        } else if (mouseY < 75) { // Hovering children
            tooltip.add(translate("children.explanation", name));
        } else { // Hovering parents
            tooltip.add(translate("parents.explanation", name));
        };
    };

    @Override
    public void draw(FlagInfoRecipe<STACK> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, Flag.getNameColored(recipe.flag()), recipe.stack().isPresent() ? 20 : 0, 3, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("preservationProportion", Lang.ONE_DP_DF.format(recipe.flag().value().getPreservationProportion() * 100)), 0, 20, 0xFF808080, false);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("children"), 0, 32, 0xFF808080, false);
        guiGraphics.drawString(Minecraft.getInstance().font, translate("parents"), 0, 77, 0xFF808080, false);
        guiGraphics.fill(0, 42, 153, 75, 0xFF8B8B8B);
        guiGraphics.fill(0, 87, 153, 120, 0xFF8B8B8B);
    };

    protected final Component translate(String suffix, Object ... args) {
        return Lang.translate("jei.flags." + suffix, args);
    };

    public record FlagInfoRecipe<STACK>(Optional<Pair<STACK, IFlagPole<?, STACK>>> stack, Holder<Flag> flag) {

        public static FlagInfoRecipe<ItemStack> forItemStacks(Holder<Flag> flag) {
            return new FlagInfoRecipe<>(Optional.empty(), flag);
        };
    };
};
