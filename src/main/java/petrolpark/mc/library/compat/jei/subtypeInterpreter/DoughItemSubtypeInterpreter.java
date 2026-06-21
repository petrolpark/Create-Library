package petrolpark.mc.library.compat.jei.subtypeInterpreter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.IDough;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class DoughItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final DoughItemSubtypeInterpreter INSTANCE = new DoughItemSubtypeInterpreter();

    @Override
    public @Nullable IDough getSubtypeData(ItemStack ingredient, UidContext context) {
        final DoughData data = ingredient.get(PetrolparkCreateDataComponentTypes.DOUGH);
        return data == null ? null : data.dough();
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    };
    
};
