package petrolpark.mc.library.core.world.restaurant.gui;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.phys.Vec2;

public record RestaurantScreenJEIHandler<SCREEN extends RestaurantScreen<?>>(IIngredientManager ingredientManager) implements IScreenHandler<SCREEN> {

    @Override
    public @Nullable IGuiProperties apply(SCREEN guiScreen) {
        return null;
    };

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(@Nonnull IClickableIngredientFactory factory, @Nonnull SCREEN screen, double mouseX, double mouseY) {
        final Vec2 mouseOnNote = screen.getMouseOnNote(mouseX, mouseY);
        if (mouseOnNote == null) return Optional.empty();
        return screen.getNoteStackUnderMouse(mouseOnNote)
            .flatMap(his -> ingredientManager().getClickableIngredientFactory()
                .createBuilder(his.stack())
                .buildWithArea(his.x() + screen.getGuiLeft() + RestaurantScreen.NOTE_X, his.y() + screen.getGuiTop() + (int)screen.getNoteY(0f), 16, 16)
            );
    };
    
};
