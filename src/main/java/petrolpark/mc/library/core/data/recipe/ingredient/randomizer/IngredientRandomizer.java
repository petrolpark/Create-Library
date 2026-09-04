package petrolpark.mc.library.core.data.recipe.ingredient.randomizer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface IngredientRandomizer extends LootContextUser {

    /**
     * Use {@link IngredientRandomizer#DIRECT_CODEC} instead.
     */
    static final Codec<IngredientRandomizer> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_RANDOMIZER_TYPES
        .byNameCodec()
        .dispatch("ingredient_randomizer_type", IngredientRandomizer::getType, IngredientRandomizerType::codec);

    public static final Codec<IngredientRandomizer> DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, FromArrayIngredientRandomizer.INLINE_CODEC));
    
    public IAdvancedIngredient<ItemStack> generate(LootContext context);

    public IngredientRandomizerType getType();
};
