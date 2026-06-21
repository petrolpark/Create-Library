package petrolpark.mc.library.core.data.recipe.bogglePattern;

import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

public record BogglePatternAdvancedIngredient(BogglePattern pattern) implements IAdvancedIngredient<MutableDataComponentHolder> {

    @Override
    public boolean test(MutableDataComponentHolder stack) {
        Integer pattern = stack.get(PetrolparkDataComponentTypes.BOGGLE_PATTERN);
        if (pattern == null) return false;
        return (int)pattern == pattern().getPattern();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToDescription'");
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addToCounterDescription'");
    };

    @Override
    public IAdvancedIngredientType<? super MutableDataComponentHolder> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
