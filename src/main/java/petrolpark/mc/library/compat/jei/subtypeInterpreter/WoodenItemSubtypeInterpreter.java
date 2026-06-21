package petrolpark.mc.library.compat.jei.subtypeInterpreter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.WoodHelper.Wood;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class WoodenItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final WoodenItemSubtypeInterpreter INSTANCE = new WoodenItemSubtypeInterpreter();

    @Override
    public @Nullable Wood getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(PetrolparkDataComponentTypes.WOOD);
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    };
    
};
