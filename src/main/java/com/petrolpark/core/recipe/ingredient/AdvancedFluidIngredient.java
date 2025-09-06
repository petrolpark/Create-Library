package com.petrolpark.core.recipe.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientTypes;
import com.petrolpark.core.recipe.ingredient.advanced.FluidAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class AdvancedFluidIngredient extends FluidIngredient {

    public static final MapCodec<AdvancedFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidAdvancedIngredient.CODEC.fieldOf("ingredient").forGetter(AdvancedFluidIngredient::getAdvancedIngredient)
    ).apply(instance, AdvancedFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedFluidIngredient> STREAM_CODEC = StreamCodec.composite(
        FluidAdvancedIngredient.STREAM_CODEC, AdvancedFluidIngredient::getAdvancedIngredient,
        AdvancedFluidIngredient::new
    );

    protected final IAdvancedIngredient<? super FluidStack> advancedIngredient;

    public AdvancedFluidIngredient(IAdvancedIngredient<? super FluidStack> ingredient) {
        this.advancedIngredient = ingredient;
    };

    public IAdvancedIngredient<? super FluidStack> getAdvancedIngredient() {
        return advancedIngredient;
    };

    @Override
    public boolean test(FluidStack stack) {
        return advancedIngredient.test(stack);
    };

    @Override
    protected Stream<FluidStack> generateStacks() {
        return advancedIngredient.streamExamples().map(s -> s instanceof FluidStack fluidStack ? fluidStack : null);
    };

    @Override
    public boolean isSimple() {
        return false;
    };

    @Override
    public FluidIngredientType<?> getType() {
        return PetrolparkIngredientTypes.FLUID_ADVANCED.get();
    };

    @Override
    public int hashCode() {
        return 31 * advancedIngredient.hashCode();
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof AdvancedFluidIngredient ingredient && ingredient.advancedIngredient.equals(ingredient);
    };
    
};
