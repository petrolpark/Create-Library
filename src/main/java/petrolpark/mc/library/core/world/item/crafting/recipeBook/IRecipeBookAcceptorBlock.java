package petrolpark.mc.library.core.world.item.crafting.recipeBook;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRecipeBookAcceptorBlock {
    
    public default void addProxyRecipeBookAcceptorPositions(Level level, BlockPos pos, BlockState state, Consumer<BlockPos> posAdder) {};

    public default void onAvailableRecipesChanged(Level level, BlockPos pos, BlockState state) {};

    public boolean acceptsRecipeBook(Level level, BlockPos pos, BlockState state, RecipeHolder<?> recipeHolder);
};
