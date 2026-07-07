package petrolpark.mc.library.core.data.recipe.ingredient.randomizer;

import com.mojang.serialization.Codec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public interface IngredientRandomizer extends LootContextUser {

    /**
     * Use {@link IngredientRandomizer#CODEC} instead.
     */
    static final Codec<IngredientRandomizer> TYPED_CODEC = PetrolparkRegistries.INGREDIENT_RANDOMIZER_TYPES
        .byNameCodec()
        .dispatch(IngredientRandomizer::getType, IngredientRandomizerType::codec);

    public static final Codec<IngredientRandomizer> CODEC = null;
    
    public IAdvancedIngredient<? super ItemStack> generate(LootContext context);

    public IngredientRandomizerType getType();
};
