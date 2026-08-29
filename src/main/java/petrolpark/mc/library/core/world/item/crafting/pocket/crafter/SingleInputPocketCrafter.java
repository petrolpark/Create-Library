package petrolpark.mc.library.core.world.item.crafting.pocket.crafter;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import petrolpark.mc.library.core.world.item.crafting.pocket.IPocketCraftingContext;
import petrolpark.mc.library.core.world.item.crafting.pocket.interpretedSlot.IInterpretedSlot;

@ParametersAreNonnullByDefault
public abstract class SingleInputPocketCrafter<R extends Recipe<SingleRecipeInput>> extends SimplePocketCrafter<SingleRecipeInput, R> {

    @Override
    @Nullable
    public SingleRecipeInput createRecipeInput(IPocketCraftingContext context, List<IInterpretedSlot<?>> slots) {
        if (slots.size() != 1) return null;
        return new SingleRecipeInput(slots.get(0).slot().getItem());
    };
    
};
