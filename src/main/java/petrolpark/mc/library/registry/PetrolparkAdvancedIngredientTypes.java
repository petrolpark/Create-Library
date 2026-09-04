package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.CompoundAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.EnchantmentItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.FlaggedAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.GenericAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.HolderSetFluidAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.HolderSetItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemIDRegExAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.NotAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.PassAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.TagItemAdvancedIngredient;

public class PetrolparkAdvancedIngredientTypes {


    // Items
    public static final RegistryEntry<IAdvancedIngredientType<ItemStack>, PassAdvancedIngredient<ItemStack>>

    ITEM_PASS = REGISTRATE.itemAdvancedIngredientType("pass", PassAdvancedIngredient::new);

    public static final RegistryEntry<IAdvancedIngredientType<ItemStack>, ? extends IAdvancedIngredientType<ItemStack>>
    
    ITEM_FLAGGED = REGISTRATE.itemAdvancedIngredientType("flagged", FlaggedAdvancedIngredient.Type::new);
    
    public static final RegistryEntry<IAdvancedIngredientType<ItemStack>, ? extends INamedAdvancedIngredientType<ItemStack>> 
    
    ITEM = REGISTRATE.namedItemAdvancedIngredientType("item", ItemItemAdvancedIngredient.Type::new),
    ITEM_ID_REGEX = REGISTRATE.itemAdvancedIngredientType("id_regex", ItemIDRegExAdvancedIngredient.CODEC, ItemIDRegExAdvancedIngredient.STREAM_CODEC),
    ITEM_HOLDER_SET = REGISTRATE.itemAdvancedIngredientType("set", HolderSetItemAdvancedIngredient.CODEC, HolderSetItemAdvancedIngredient.STREAM_CODEC),
    ITEM_TAG = REGISTRATE.namedItemAdvancedIngredientType("tag", TagItemAdvancedIngredient.Type::new),
    ITEM_ENCHANTMENTS = REGISTRATE.namedItemAdvancedIngredientType("enchantments", EnchantmentItemAdvancedIngredient.Type::new);
    
    public static final RegistryEntry<IAdvancedIngredientType<ItemStack>, GenericAdvancedIngredientType<ItemStack, NotAdvancedIngredient<ItemStack>>> ITEM_NOT = REGISTRATE.itemAdvancedIngredientType("not", NotAdvancedIngredient::codec, NotAdvancedIngredient::streamCodec);
    public static final RegistryEntry<IAdvancedIngredientType<ItemStack>, GenericAdvancedIngredientType<ItemStack, CompoundAdvancedIngredient<ItemStack>>> ITEM_COMPOUND = REGISTRATE.itemAdvancedIngredientType("compound", CompoundAdvancedIngredient::codec, CompoundAdvancedIngredient::streamCodec);

    // Fluids
    public static final RegistryEntry<IAdvancedIngredientType<FluidStack>, PassAdvancedIngredient<FluidStack>>

    FLUID_PASS = REGISTRATE.fluidAdvancedIngredientType("pass", PassAdvancedIngredient::new);

    public static final RegistryEntry<IAdvancedIngredientType<FluidStack>, ? extends IAdvancedIngredientType<FluidStack>>

    FLUID_NOT = REGISTRATE.fluidAdvancedIngredientType("not", NotAdvancedIngredient::codec, NotAdvancedIngredient::streamCodec),
    FLUID_FLAGGED = REGISTRATE.fluidAdvancedIngredientType("flagged", FlaggedAdvancedIngredient.Type::new);

    public static final RegistryEntry<IAdvancedIngredientType<FluidStack>, ? extends INamedAdvancedIngredientType<FluidStack>> 

    FLUID_HOLDER_SET = REGISTRATE.fluidAdvancedIngredientType("set", HolderSetFluidAdvancedIngredient.CODEC, HolderSetFluidAdvancedIngredient.STREAM_CODEC);
    
    public static final void register() {};
};
