package com.petrolpark.recipe.advancedprocessing.firsttimelucky;

import java.util.List;
import java.util.function.Predicate;

import com.petrolpark.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.recipe.RecipeFinder;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.LazyOptional;

public class FirstTimeLuckyRecipesBehaviour extends AbstractRememberPlacerBehaviour {

    public static final BehaviourType<FirstTimeLuckyRecipesBehaviour> TYPE = new BehaviourType<>();

    private final Object recipeCacheKey = new Object();
    private final Predicate<Recipe<?>> recipeFilter;

    /**
     * Ensure this Block Entity remembers who placed it for the purposes of ensuring first-time-lucky
     * recipes award all outputs.
     * @param be
     * @param recipeFilter all recipes which match this filter and implement {@link com.petrolpark.destroy.recipe.IFirstTimeLuckyRecipe IFirstTimeLuckyRecipe}
     * will be checked - if there is at least one of them we haven't done, we'll remember the player
     */
    public FirstTimeLuckyRecipesBehaviour(SmartBlockEntity be, Predicate<Recipe<?>> recipeFilter) {
        super(be);
        this.recipeFilter = recipeFilter;
    };

    @Override
    public boolean shouldRememberPlacer(Player placer) {
        List<Recipe<?>> applicableRecipes = RecipeFinder.get(recipeCacheKey, getWorld(), recipeFilter.and(r -> r instanceof IFirstTimeLuckyRecipe));
        if (applicableRecipes.isEmpty()) return false; // If there are no recipes which need to be lucky
        LazyOptional<FirstTimeLuckyRecipesCapability> plfrOp = placer.getCapability(FirstTimeLuckyRecipesCapability.Provider.PLAYER_LUCKY_FIRST_RECIPES);
        if (!plfrOp.isPresent()) return true;
        FirstTimeLuckyRecipesCapability plfr = plfrOp.resolve().get();
        return !applicableRecipes.stream().map(Recipe::getId).allMatch(plfr::contains);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
