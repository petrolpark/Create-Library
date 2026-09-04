package petrolpark.mc.library.core.data.recipe.ingredient;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkNeoForgeIngredientTypes;

public record AdvancedItemIngredient(IAdvancedIngredient<ItemStack> advacnedIngredient) implements ICustomIngredient {

    public static final MapCodec<AdvancedItemIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemAdvancedIngredient.CODEC.fieldOf("ingredient").forGetter(AdvancedItemIngredient::advacnedIngredient)
    ).apply(instance, AdvancedItemIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedItemIngredient> STREAM_CODEC = StreamCodec.composite(
        ItemAdvancedIngredient.STREAM_CODEC, AdvancedItemIngredient::advacnedIngredient,
        AdvancedItemIngredient::new
    );

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        return advacnedIngredient().test(stack);
    };

    @Override
    public Stream<ItemStack> getItems() {
        return advacnedIngredient().streamExamples().map(s -> s instanceof ItemStack stack ? stack : null);
    };

    @Override
    public boolean isSimple() {
        return false;
    };

    @Override
    public IngredientType<AdvancedItemIngredient> getType() {
        return PetrolparkNeoForgeIngredientTypes.ADVANCED.get();
    };
    
};
