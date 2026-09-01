package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.tterrag.registrate.util.entry.RegistryEntry;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.item.attribute.CompressedItemAttribute;
import petrolpark.mc.library.compat.create.core.world.item.attribute.FlaggedItemAttribute;
import petrolpark.mc.library.compat.create.core.world.item.attribute.SimplePetrolparkItemAttribute;
import petrolpark.mc.library.compat.create.core.world.item.attribute.WoodItemAttribute;
import petrolpark.mc.library.util.ItemHelper;

@RequiresCreate
public class PetrolparkItemAttributeTypes {
    
    public static final RegistryEntry<ItemAttributeType, SimplePetrolparkItemAttribute>
    
    IS_ANIMAL_FOOD = REGISTRATE.simpleItemAttributeType("is_animal_food", (stack, level) -> ItemHelper.getKnownAnimalFoods(level).contains(stack.getItem()));

    public static final RegistryEntry<ItemAttributeType, FlaggedItemAttribute.Type> FLAGGED = REGISTRATE.itemAttributeType("flagged", new FlaggedItemAttribute.Type());
    public static final RegistryEntry<ItemAttributeType, CompressedItemAttribute.Type> COMPRESSED = REGISTRATE.itemAttributeType("compressed", new CompressedItemAttribute.Type());
    public static final RegistryEntry<ItemAttributeType, WoodItemAttribute.Type> WOOD = REGISTRATE.itemAttributeType("wood", new WoodItemAttribute.Type());

    public static final void init() {};
};
