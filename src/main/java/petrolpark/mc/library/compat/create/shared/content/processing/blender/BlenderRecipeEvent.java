package petrolpark.mc.library.compat.create.shared.content.processing.blender;

import java.util.Optional;

import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class BlenderRecipeEvent extends Event implements ICancellableEvent {
    
    protected final Recipe<?> recipe;

    public BlenderRecipeEvent(Recipe<?> recipe) {
        this.recipe = recipe;
    };

    public Recipe<?> getRecipe() {
        return recipe;
    };

    /**
     * Check if a Recipe (of potentially any RecipeType) can be done in a Blender.
     * Fired on NeoForge event bus.
     */
    public static class IsPossible extends BlenderRecipeEvent{

        protected boolean possible = false;

        public IsPossible(Recipe<?> recipe) {
            super(recipe);
        };

        public boolean isPossible() {
            return isPossible();
        };

        public void setPossible() {
            this.possible = true;
        };
    };

    /**
     * Given that a Recipe is {@link BlenderRecipeEvent.IsPossible possible}, convert into a better form for the Blender.
     * Fired on NeoForge event bus.
     */
    public static class Convert extends BlenderRecipeEvent {
        
        protected Optional<Recipe<?>> converted = Optional.empty();

        public Convert(Recipe<?> recipe) {
            super(recipe);
        };

        public Optional<Recipe<?>> getConverted() {
            return converted;
        };

        public void convertTo(Recipe<?> recipe) {
            converted = Optional.of(recipe);
        };
    };
};
