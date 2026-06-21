package petrolpark.mc.library.core.world.item.crafting.recipeBook;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface IRecipeBookAcceptorBlockEntity {

    /**
     * If the Block implements {@link IRecipeBookAcceptorBlock}, this does not need to duplicate what was given there.
     * @param posAdder
     */
    public default void addProxyRecipeBookAcceptorPositions(Consumer<BlockPos> posAdder) {};

    public default void onAvailableRecipesChanged() {};

    public boolean acceptsRecipeBook(RecipeHolder<?> recipeHolder);
};
