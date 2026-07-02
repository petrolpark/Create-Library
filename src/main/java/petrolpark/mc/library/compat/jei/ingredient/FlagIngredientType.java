package petrolpark.mc.library.compat.jei.ingredient;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import petrolpark.mc.library.compat.jei.ingredient.FlagIngredientType.FlagHolderHolder;
import petrolpark.mc.library.core.client.rendering.PetrolparkGuiTexture;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.Lang;

/**
 * {@link Flag} {@link IIngredientType}
 */
public class FlagIngredientType implements IIngredientType<FlagHolderHolder> {

    public static final FlagIngredientType TYPE = new FlagIngredientType();
    public static final HolderIngredientHelper<Flag, FlagHolderHolder> HELPER = new HolderIngredientHelper<>(TYPE, PetrolparkRegistries.Keys.FLAG, FlagHolderHolder::new);
    public static final FlagIngredientType.EmptyRenderer EMPTY_RENDERER = new FlagIngredientType.EmptyRenderer();
    /**
     * Renders a Flag icon in its color.
     */
    public static final FlagIngredientType.IconRenderer ICON_RENDERER = new FlagIngredientType.IconRenderer();
    /**
     * Renders the name of the Flag in its color, shortened to fit within 150 pixels. The background is not rendered, so this should be used with {@link #BACKGROUND}.
     */
    public static final FlagIngredientType.FullRenderer FULL_RENDERER = new FlagIngredientType.FullRenderer();

    public static final IDrawable BACKGROUND = new IDrawable() {

        @Override
        public int getWidth() {
            return 152;
        };

        @Override
        public int getHeight() {
            return 11;
        };

        @Override
        public void draw(@Nonnull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.fill(xOffset, yOffset, xOffset + 152, yOffset + 11, 0xFF8B8B8B);
        };
        
    };

    public record FlagHolderHolder(Holder<Flag> holder) implements HolderIngredientHelper.HolderHolder<Flag> {};

    @Override
    public Class<FlagHolderHolder> getIngredientClass() {
        return FlagHolderHolder.class;
    };

    @ParametersAreNonnullByDefault
    public static class EmptyRenderer implements IIngredientRenderer<FlagHolderHolder> {

        @Override
        public void render(GuiGraphics guiGraphics, FlagHolderHolder ingredient) {
            
        };

        @Override
        public List<Component> getTooltip(FlagHolderHolder ingredient, TooltipFlag tooltipFlag) {
            return Collections.singletonList(Flag.getNameColored(ingredient.holder()));
        };

    };

    @ParametersAreNonnullByDefault
    public static class IconRenderer extends EmptyRenderer {

        @Override
        public void render(GuiGraphics guiGraphics, FlagHolderHolder ingredient) {
            render(guiGraphics, 0xFF000000 | ingredient.value().getColor());
        };

        public void render(GuiGraphics guiGraphics, int color) {
            PetrolparkGuiTexture.JEI_FLAGPOLE.render(guiGraphics, 0, 0);
            PetrolparkGuiTexture.JEI_FLAG.render(guiGraphics, 0, 0, color);
        };
        
    };

    /**
     * @see FlagIngredientType#FULL_RENDERER
     */
    @ParametersAreNonnullByDefault
    public static class FullRenderer extends EmptyRenderer {

        @Override
        public void render(GuiGraphics guiGraphics, FlagHolderHolder ingredient) {
            final Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, Lang.shorten(Flag.getName(ingredient.holder()).getString(), font, 148), 1, 0, ingredient.value().getColor());
        };

        @Override
        public int getWidth() {
            return 150;
        };

        @Override
        public int getHeight() {
            return 9;
        };

    };

    public static class Icon implements IDrawable {

        public final int color;

        public Icon(int color) {
            this.color = color;
        };

        @Override
        public int getWidth() {
            return 16;
        };

        @Override
        public int getHeight() {
            return 16;
        };

        @Override
        public void draw(@Nonnull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(xOffset, yOffset, 0f);
            ICON_RENDERER.render(guiGraphics, color);
            guiGraphics.pose().popPose();
        };

    };
    
};
