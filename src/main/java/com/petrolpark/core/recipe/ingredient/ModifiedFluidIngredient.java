package com.petrolpark.core.recipe.ingredient;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientTypes;
import com.petrolpark.core.recipe.ingredient.modifier.FluidIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class ModifiedFluidIngredient extends FluidIngredient {

    public static final MapCodec<ModifiedFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidIngredientModifier.CODEC.fieldOf("modifier").forGetter(ModifiedFluidIngredient::getModifier)
    ).apply(instance, ModifiedFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedFluidIngredient> STREAM_CODEC = StreamCodec.composite(
        FluidIngredientModifier.STREAM_CODEC, ModifiedFluidIngredient::getModifier,
        ModifiedFluidIngredient::new
    );

    protected final IIngredientModifier<? super FluidStack> modifier;

    public ModifiedFluidIngredient(IIngredientModifier<? super FluidStack> modifier) {
        this.modifier = modifier;
    };

    public IIngredientModifier<? super FluidStack> getModifier() {
        return modifier;
    };

    @Override
    public boolean test(FluidStack stack) {
        return modifier.test(stack);
    };

    @Override
    protected Stream<FluidStack> generateStacks() {
        return modifier.streamExamples().map(s -> s instanceof FluidStack fluidStack ? fluidStack : null);
    };

    @Override
    public boolean isSimple() {
        return false;
    };

    @Override
    public FluidIngredientType<?> getType() {
        return PetrolparkIngredientTypes.FLUID_MODIFIED.get();
    };

    @Override
    public int hashCode() {
        return 31 * modifier.hashCode();
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof ModifiedFluidIngredient ingredient && ingredient.modifier.equals(modifier);
    };
    
};
