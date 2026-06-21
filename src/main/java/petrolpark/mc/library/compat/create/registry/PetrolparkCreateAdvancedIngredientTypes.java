package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.data.recipe.ingredient.advanced.CreateItemAttributeAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;

public class  PetrolparkCreateAdvancedIngredientTypes {

    public static final RegistryEntry<IAdvancedIngredientType<? super ItemStack>, INamedAdvancedIngredientType<ItemStack>> 

    ITEM_ATTRIBUTE = REGISTRATE.itemAdvancedIngredientType("create_item_attribute", CreateItemAttributeAdvancedIngredient.Type::new);

    public static final void register() {};
};
