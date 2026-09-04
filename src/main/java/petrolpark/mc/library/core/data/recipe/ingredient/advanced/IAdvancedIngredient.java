package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

public interface IAdvancedIngredient<STACK> extends ITypelessAdvancedIngredient<STACK> {

    public IAdvancedIngredientType<STACK> getType();

    @Override
    public default IAdvancedIngredient<STACK> simplify() {
        return this;
    };
};
