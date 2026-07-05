package petrolpark.mc.library.compat.create.core.world.dough;

import static petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes.DOUGH;

import java.util.Collections;
import java.util.Optional;

import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.block.entity.press.PressingRecipeSearchEvent;

public class DoughRecipes {
    
    public static final void onPressingRecipeSearch(PressingRecipeSearchEvent event) {
        final DoughData data = event.getStack().get(DOUGH);
        if (data == null) return;
        DoughData resultData = data;
        boolean success = false;
        for (boolean lengthwise : Iterate.trueAndFalse) {
            if (data.isRollable(lengthwise)) {
                resultData = resultData.rolled(lengthwise, false);
                success = true;
            };
        };
        if (success) {
            final ResourceLocation id = Petrolpark.asResource("dough_pressing");
            final ItemStack result = event.getStack().copy();
            result.set(DOUGH, resultData);
            event.addRecipe(() -> Optional.of(new RecipeHolder<>(id, 
                new StandardProcessingRecipe.Builder<>(PressingRecipe::new, id)
                    .require(
                        new DataComponentIngredient(
                            HolderSet.direct(Collections.singletonList(BuiltInRegistries.ITEM.wrapAsHolder(event.getStack().getItem()))),
                            DataComponentPredicate.builder().expect(DOUGH, data).build(),
                            true
                        )
                    ).output(result)
                    .build()
                )), 100
            );  
        };
    };
};