package petrolpark.mc.library.compat.create.core.data.recipe.firstTimeLucky;

import java.util.function.Predicate;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateAttachmentTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;

@RequiresCreate
public class FTLRecipesBehaviour extends AbstractRememberPlacerBehaviour {

    public static final BehaviourType<FTLRecipesBehaviour> TYPE = new BehaviourType<>();

    private final Object recipeCacheKey = new Object();
    private final Predicate<RecipeHolder<?>> recipeFilter;

    /**
     * Ensure this Block Entity remembers who placed it for the purposes of ensuring first-time-lucky
     * recipes award all outputs.
     * @param be
     * @param recipeFilter all recipes which match this filter and implement {@link IFTLProcessingRecipe}
     * will be checked - if there is at least one of them we haven't done, we'll remember the player
     */
    public FTLRecipesBehaviour(SmartBlockEntity be, Predicate<RecipeHolder<?>> recipeFilter) {
        super(be);
        this.recipeFilter = recipeFilter;
    };

    @Override
    public boolean shouldRememberPlacer(Player placer) {
        return !RecipeFinder.get(recipeCacheKey, getWorld(), recipeFilter.and(
            rh -> rh.value() instanceof IFTLProcessingRecipe recipe
            && recipe.getFirstTimeLuckyKey().isPresent()
            && !placer.getData(PetrolparkCreateAttachmentTypes.FTL_RECIPES).contains(recipe.getFirstTimeLuckyKey().get())
        )).isEmpty();
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
