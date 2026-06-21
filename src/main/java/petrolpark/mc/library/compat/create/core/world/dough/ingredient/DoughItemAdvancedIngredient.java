package petrolpark.mc.library.compat.create.core.world.dough.ingredient;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record DoughItemAdvancedIngredient(List<IAdvancedIngredient<DoughData>> doughIngredients) implements ItemAdvancedIngredient {

    public static final MapCodec<DoughItemAdvancedIngredient> CODEC = CodecHelper.singleFieldMap(DoughIngredient.STRICT_CODEC.listOf(), "dough_ingredients", DoughItemAdvancedIngredient::doughIngredients, DoughItemAdvancedIngredient::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DoughItemAdvancedIngredient> STREAM_CODEC = StreamCodec.composite(DoughIngredient.STRICT_STREAM_CODEC.apply(ByteBufCodecs.list()), DoughItemAdvancedIngredient::doughIngredients, DoughItemAdvancedIngredient::new);

    @Override
    public void addToDescription(IndentedTooltipBuilder description) {
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            ingredient.addToDescription(description);
        };
    };

    @Override
    public void addToCounterDescription(IndentedTooltipBuilder description) {
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            ingredient.addToCounterDescription(description);
        };
    };

    @Override
    public boolean test(ItemStack stack) {
        final DoughData data = stack.get(PetrolparkCreateDataComponentTypes.DOUGH);
        return data != null && doughIngredients().stream().allMatch(ingredient -> ingredient.test(data));
    };

    @Override
    public Stream<? extends ItemStack> streamExamples() {
        Stream<DoughData> stream = doughIngredients.stream()
            .flatMap(IAdvancedIngredient::streamExamples);
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            stream = ingredient.modifyExamples(stream);
        };
        return stream.map(data -> {
            final ItemStack stack = SharedCreateBlocks.DOUGH.asStack();
            stack.set(PetrolparkCreateDataComponentTypes.DOUGH, data);
            return stack;
        });
    };
    
    @Override
    public Stream<? extends ItemStack> streamCounterExamples() {
        Stream<DoughData> stream = doughIngredients.stream()
            .flatMap(IAdvancedIngredient::streamCounterExamples);
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            stream = ingredient.modifyCounterExamples(stream);
        };
        return stream.map(data -> {
            final ItemStack stack = SharedCreateBlocks.DOUGH.asStack();
            stack.set(PetrolparkCreateDataComponentTypes.DOUGH, data);
            return stack;
        });
    };

    @Override
    public Stream<ItemStack> modifyExamples(Stream<ItemStack> exampleStacks) {
        exampleStacks = exampleStacks.filter(stack -> stack.has(PetrolparkCreateDataComponentTypes.DOUGH));
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            exampleStacks = exampleStacks.map(stack -> {
                stack.set(PetrolparkCreateDataComponentTypes.DOUGH, ingredient.modifyExamples(Stream.of(stack.get(PetrolparkCreateDataComponentTypes.DOUGH))).findAny().orElse(null));  
                return stack;
            });
        };
        return exampleStacks;
    };

    @Override
    public Stream<ItemStack> modifyCounterExamples(Stream<ItemStack> counterExampleStacks) {
        for (IAdvancedIngredient<DoughData> ingredient : doughIngredients()) {
            counterExampleStacks = counterExampleStacks.map(stack -> {
                if (!stack.has(PetrolparkCreateDataComponentTypes.DOUGH)) return stack;
                stack.set(PetrolparkCreateDataComponentTypes.DOUGH, ingredient.modifyCounterExamples(Stream.of(stack.get(PetrolparkCreateDataComponentTypes.DOUGH))).findAny().orElse(null));  
                return stack;
            });
        };
        return counterExampleStacks;
    };

    @Override
    public INamedAdvancedIngredientType<ItemStack> getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
