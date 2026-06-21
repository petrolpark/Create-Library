package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;

public record ItemIngredientValueAdvancedIngredient(Ingredient.Value ingredientValue) implements ItemAdvancedIngredient {

    @Override
    public boolean test(ItemStack stack) {
        if (ingredientValue() instanceof ItemValue itemValue) return stack.is(itemValue.item().getItem());
        else if (ingredientValue() instanceof TagValue tagValue) return stack.is(tagValue.tag());
        return false;
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
    public NamedAdvancedIngredientType<ItemStack> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
