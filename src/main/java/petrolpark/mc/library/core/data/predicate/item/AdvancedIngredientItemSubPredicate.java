package petrolpark.mc.library.core.data.predicate.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;

public record AdvancedIngredientItemSubPredicate(IAdvancedIngredient<? super ItemStack> ingredient) implements ItemSubPredicate {

    public static final Codec<AdvancedIngredientItemSubPredicate> CODEC = CodecHelper.singleField(ItemAdvancedIngredient.CODEC, "ingredient", AdvancedIngredientItemSubPredicate::ingredient, AdvancedIngredientItemSubPredicate::new);

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        return ingredient().test(stack);
    };
    
};
