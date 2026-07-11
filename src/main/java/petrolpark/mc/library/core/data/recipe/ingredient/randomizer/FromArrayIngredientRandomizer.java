package petrolpark.mc.library.core.data.recipe.ingredient.randomizer;

import java.util.List;
import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.registry.PetrolparkIngredientRandomizerTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public record FromArrayIngredientRandomizer(List<FromArrayIngredientRandomizer.Entry> entries) implements IngredientRandomizer {

    public static final MapCodec<FromArrayIngredientRandomizer> CODEC = CodecHelper.singleFieldMap(FromArrayIngredientRandomizer.Entry.LIST_CODEC, "ingredients", FromArrayIngredientRandomizer::entries, FromArrayIngredientRandomizer::new);
    public static final Codec<FromArrayIngredientRandomizer> INLINE_CODEC = FromArrayIngredientRandomizer.Entry.LIST_CODEC.xmap(FromArrayIngredientRandomizer::new, FromArrayIngredientRandomizer::entries);

    @Override
    public IAdvancedIngredient<? super ItemStack> generate(LootContext context) {
        if (entries().size() == 0) return ItemAdvancedIngredient.impossible();
        if (entries().size() == 1) return entries().get(1).ingredient();

        final List<FromArrayIngredientRandomizer.RolledEntry> weightedEntries = entries().stream().map(FromArrayIngredientRandomizer.roll(context)).toList();
        final double sum = weightedEntries.stream().mapToDouble(FromArrayIngredientRandomizer.RolledEntry::weight).sum();
        double value = context.getRandom().nextDouble() * sum;
        for (FromArrayIngredientRandomizer.RolledEntry entry : weightedEntries) {
            value -= entry.weight();
            if (value <= 0f) return entry.ingredient();
        };

        return ItemAdvancedIngredient.impossible(); // Unreachable
    };

    @Override
    public IngredientRandomizerType getType() {
        return PetrolparkIngredientRandomizerTypes.FROM_ARRAY.get();
    };

    private static final Function<FromArrayIngredientRandomizer.Entry, FromArrayIngredientRandomizer.RolledEntry> roll(LootContext context) {
        return entry -> new RolledEntry(entry.ingredient(), entry.weight().getFloat(context));
    };

    public record Entry(IAdvancedIngredient<? super ItemStack> ingredient, NumberProvider weight) {

        public static final Codec<List<FromArrayIngredientRandomizer.Entry>> LIST_CODEC = CodecHelper.listOrSingle(Codec.withAlternative(
            RecordCodecBuilder.create(instance ->
                instance.group(
                    ItemAdvancedIngredient.CODEC.fieldOf("ingredient").forGetter(FromArrayIngredientRandomizer.Entry::ingredient),
                    NumberProviders.CODEC.optionalFieldOf("weight", ConstantValue.exactly(1f)).forGetter(FromArrayIngredientRandomizer.Entry::weight)
                ).apply(instance, FromArrayIngredientRandomizer.Entry::new)
            ),
            ItemAdvancedIngredient.CODEC.xmap(FromArrayIngredientRandomizer.Entry::new, FromArrayIngredientRandomizer.Entry::ingredient)
        ));

        public Entry(IAdvancedIngredient<? super ItemStack> ingredient) {
            this(ingredient, ConstantValue.exactly(1f)); 
        };
    };

    record RolledEntry(IAdvancedIngredient<? super ItemStack> ingredient, float weight) {

    };

    @Override
    public void validate(ValidationContext context) {
        IngredientRandomizer.super.validate(context);
        for (int i = 0; i < entries().size(); i++) entries().get(i).weight().validate(context.forChild("entry.weight[" + i + "]"));
    };
    
};
