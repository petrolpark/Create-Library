package com.petrolpark.core.recipe.ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkIngredientTypes;
import com.petrolpark.core.recipe.ingredient.modifier.FluidIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public class ModifiedFluidIngredient extends FluidIngredient {

    public static final MapCodec<ModifiedFluidIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidIngredient.CODEC.fieldOf("ingredient").forGetter(ModifiedFluidIngredient::getIngredient),
        FluidIngredientModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ModifiedFluidIngredient::getModifiers)
    ).apply(instance, ModifiedFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedFluidIngredient> STREAM_CODEC = StreamCodec.composite(
        FluidIngredient.STREAM_CODEC, ModifiedFluidIngredient::getIngredient,
        FluidIngredientModifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ModifiedFluidIngredient::getModifiers,
        ModifiedFluidIngredient::new
    );

    protected final FluidIngredient ingredient;
    protected final List<IIngredientModifier<? super FluidStack>> modifiers;

    public ModifiedFluidIngredient(FluidIngredient ingredient, List<IIngredientModifier<? super FluidStack>> modifiers) {
        this.ingredient = ingredient;
        this.modifiers = modifiers;
    };

    public FluidIngredient getIngredient() {
        return ingredient;
    };

    public List<IIngredientModifier<? super FluidStack>> getModifiers() {
        return modifiers;
    };

    @Override
    public boolean test(FluidStack stack) {
        if (!ingredient.test(stack)) return false;
        for (IIngredientModifier<? super FluidStack> modifier : modifiers) {
            if (!modifier.test(stack)) return false;
        };
        return true;
    };

    @Override
    protected Stream<FluidStack> generateStacks() {
        List<FluidStack> items = Arrays.asList(ingredient.getStacks());
        modifiers.forEach(modifier -> modifier.modifyExamples(items));
        return items.stream();
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
        int hash = ingredient.hashCode();
        for (IIngredientModifier<? super FluidStack> modifier : modifiers) hash ^= modifier.hashCode();
        return hash;
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof ModifiedFluidIngredient ingredient && ingredient.ingredient.equals(this.ingredient) && ingredient.modifiers.equals(modifiers);
    };
    
};
