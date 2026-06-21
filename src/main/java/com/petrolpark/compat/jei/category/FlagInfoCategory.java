package com.petrolpark.compat.jei.category;

import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.jei.category.FlagInfoCategory.FlagInfoRecipe;
import com.petrolpark.compat.jei.ingredient.FlagIngredientType;
import com.petrolpark.compat.jei.ingredient.FlagIngredientType.FlagHolderHolder;
import com.petrolpark.compat.jei.widget.CustomScrollGridRecipeWidget;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.registry.PetrolparkRegistries;
import com.petrolpark.util.Lang;
import com.petrolpark.util.Pair;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

@ParametersAreNonnullByDefault
public class FlagInfoCategory extends AbstractRecipeCategory<FlagInfoRecipe> {

    public static final RecipeType<FlagInfoRecipe> RECIPE_TYPE = RecipeType.create(Petrolpark.MOD_ID, "flags", FlagInfoRecipe.class);

    public FlagInfoCategory(IGuiHelper guiHelper) {
        super(RECIPE_TYPE, Lang.translate("gui.jei.category.flags"), new FlagIngredientType.Icon(0xFF55B7F7), 170, 120);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FlagInfoRecipe recipe, IFocusGroup focuses) {
        
        if (recipe.stackAndFlagPole().isPresent()) builder.addInputSlot(0, 0)
            .addTypedIngredient(recipe.stackAndFlagPole().get().getFirst())
            .setStandardSlotBackground();

        builder.addOutputSlot(recipe.stackAndFlagPole().isPresent() ? 20 : 0, 3)
            .addIngredient(FlagIngredientType.TYPE, recipe.flag())
            .setBackground(FlagIngredientType.BACKGROUND, -1, -1)
            .setCustomRenderer(FlagIngredientType.TYPE, FlagIngredientType.FULL_RENDERER);
        builder
            .addInvisibleIngredients(RecipeIngredientRole.INPUT)
            .addIngredient(FlagIngredientType.TYPE, recipe.flag());

        // Output: children
        int children = 0;
        for (Holder<Flag> child : recipe.flag().value().getChildren()) {
            builder.addOutputSlot()
                .addIngredient(FlagIngredientType.TYPE, new FlagHolderHolder(child))
                .setCustomRenderer(FlagIngredientType.TYPE, FlagIngredientType.FULL_RENDERER)
                .setSlotName("child_" + children++);
        };

        // Input: parents
        int parents = 0;
        for (Holder<Flag> parent : recipe.flag().value().getParents()) {
            builder.addInputSlot()
                .addIngredient(FlagIngredientType.TYPE, new FlagHolderHolder(parent))
                .setCustomRenderer(FlagIngredientType.TYPE, FlagIngredientType.FULL_RENDERER)
                .setSlotName("parent" + parents++);
        };
    };

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FlagInfoRecipe recipe, IFocusGroup focuses) {
        builder.addText(translate("preservationProportion", Lang.ONE_DP_DF.format(recipe.flag().value().getPreservationProportion() * 100)), getWidth(), getHeight())
            .setPosition(0, 20)
            .setColor(0xFF808080);

        builder.addText(translate("children"), getWidth(), getHeight())
            .setPosition(0, 30)
            .setColor(0xFF808080);
        final List<IRecipeSlotDrawable> childSlots = builder.getRecipeSlots().getSlots().stream().filter(slot -> slot.getSlotName().map(name -> name.startsWith("child")).orElse(false)).toList();
        final CustomScrollGridRecipeWidget childrenWidget = new CustomScrollGridRecipeWidget(1, 3, childSlots, FlagIngredientType.BACKGROUND);
        childrenWidget.setPosition(0, 40);
        builder.addSlottedWidget(childrenWidget, childSlots);
        builder.addInputHandler(childrenWidget);

        builder.addText(translate("parents"), getWidth(), getHeight())
            .setPosition(0, 77)
            .setColor(0xFF808080);
        final List<IRecipeSlotDrawable> parentSlots = builder.getRecipeSlots().getSlots().stream().filter(slot -> slot.getSlotName().map(name -> name.startsWith("parent")).orElse(false)).toList();
        final CustomScrollGridRecipeWidget parentsWidget = new CustomScrollGridRecipeWidget(1, 3, parentSlots, FlagIngredientType.BACKGROUND);
        parentsWidget.setPosition(0, 87);
        builder.addSlottedWidget(parentsWidget, parentSlots);
        builder.addInputHandler(parentsWidget);
    };

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FlagInfoRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        final Component name = Flag.getNameColored(recipe.flag().holder());
        if (mouseY < 18) { // Hovering the title
            if (recipe.stackAndFlagPole().isPresent()) tooltip.add(translate(recipe.stackAndFlagPole().get().getSecond().isIntrinsic(recipe.flag().holder()) ? "intrinsic" : "extrinsic", name));
        } else if (mouseY < 30) { // Hovering "preservation proportion"
            final float preservationProportion = recipe.flag().value().getPreservationProportion();
            if (preservationProportion == 0f) {
                tooltip.add(translate("preservationProportion.any", name));
            } else if (preservationProportion == 1f) {
                tooltip.add(translate("preservationProportion.all", name));
            } else {
                tooltip.add(translate("preservationProportion.some", Lang.ONE_DP_DF.format(preservationProportion * 100f), name));
            };
            tooltip.add(translate("unflagged", Flag.getAbsentNameColored(recipe.flag().holder())));
            tooltip.add(translate("unflaggableIgnored"));
        } else if (mouseY < 75) { // Hovering children
            tooltip.add(translate("children.explanation", name));
        } else { // Hovering parents
            tooltip.add(translate("parents.explanation", name));
        };
    };

    public static final Component translate(String suffix, Object ... args) {
        return Lang.translate("jei.flags." + suffix, args);
    };

    public record FlagInfoRecipe(Optional<Pair<ITypedIngredient<?>, IFlagPole<?, ?>>> stackAndFlagPole, FlagHolderHolder flag) {

        public FlagInfoRecipe(Holder<Flag> flag) {
            this(Optional.empty(), new FlagHolderHolder(flag));
        };
    };

    public static final void addRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RECIPE_TYPE, RegistryUtil.getRegistry(PetrolparkRegistries.Keys.FLAG).holders()
            .map(FlagInfoRecipe::new)
            .toList()
        );
    };
};
