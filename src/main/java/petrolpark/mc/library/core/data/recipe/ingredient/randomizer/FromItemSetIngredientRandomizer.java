package petrolpark.mc.library.core.data.recipe.ingredient.randomizer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemItemAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkIngredientRandomizerTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:from_set}</p>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record FromItemSetIngredientRandomizer(HolderSet<Item> items) implements IngredientRandomizer {

    public static final MapCodec<FromItemSetIngredientRandomizer> CODEC = CodecHelper.singleFieldMap(RegistryCodecs.homogeneousList(Registries.ITEM), "items", FromItemSetIngredientRandomizer::items, FromItemSetIngredientRandomizer::new);

    @Override
    public IAdvancedIngredient<ItemStack> generate(LootContext context) {
        return new ItemItemAdvancedIngredient(items().get(context.getRandom().nextInt(items().size())).value());
    };

    @Override
    public IngredientRandomizerType getType() {
        return PetrolparkIngredientRandomizerTypes.FROM_ITEM_SET.get();
    };
    
};
