package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import java.util.stream.Stream;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

public class PassAdvancedIngredient implements IAdvancedIngredient<Object> {

    public static final PassAdvancedIngredient INSTANCE = new PassAdvancedIngredient();
    public static final IAdvancedIngredientType<Object> TYPE = new AdvancedIngredientGenericType<>(Petrolpark.translationKey("advancedIngredient.pass"), INSTANCE);

    private PassAdvancedIngredient() {};

    @Override
    public boolean test(Object stack) {
        return true;
    };

    @Override
    public Stream<Object> modifyExamples(Stream<Object> exampleStacks) {
        return exampleStacks;
    };

    @Override
    public Stream<Object> modifyCounterExamples(Stream<Object> counterExampleStacks) {
        return Stream.empty();
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {};

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {};

    @Override
    public IAdvancedIngredientType<? super Object> getType() {
        return TYPE;
    };
    
};
