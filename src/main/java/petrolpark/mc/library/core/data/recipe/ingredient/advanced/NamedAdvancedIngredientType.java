package petrolpark.mc.library.core.data.recipe.ingredient.advanced;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NamedAdvancedIngredientType<STACK>(
    String translationKey,
    MapCodec<? extends IAdvancedIngredient<STACK>> codec,
    StreamCodec<? super RegistryFriendlyByteBuf, ? extends IAdvancedIngredient<STACK>> streamCodec
) implements INamedAdvancedIngredientType<STACK> {
    
};
