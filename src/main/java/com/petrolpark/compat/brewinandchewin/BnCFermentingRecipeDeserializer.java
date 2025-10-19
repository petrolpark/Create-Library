package com.petrolpark.compat.brewinandchewin;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.common.processing.basinlid.LiddedBasinRecipe;
import com.petrolpark.core.recipe.compat.CompatRecipeDeserializer;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

@RequiresCreate
public class BnCFermentingRecipeDeserializer implements CompatRecipeDeserializer<LiddedBasinRecipe> {

    public static final ResourceLocation SERIALIZER_ID = ResourceLocation.fromNamespaceAndPath("brewinandchewin", "fermenting");

    @Override
    public ResourceLocation serializerId() {
        return SERIALIZER_ID;
    };

    @Override
    public Decoder<Optional<LiddedBasinRecipe>> decoder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decoder'");
    };

    @Override
    public ResourceLocation createId(ResourceLocation baseId) {
        return baseId.withPrefix("petrolpark/lidded_basin/");
    };

    public record FluidIngredientAndAmount(FluidIngredient ingredient, int amount, String unit) {

        public static final Codec<FluidIngredientAndAmount> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("ingredient").forGetter(FluidIngredientAndAmount::ingredient),
            Codec.INT.fieldOf("amount").forGetter(FluidIngredientAndAmount::amount),
            Codec.STRING.fieldOf("unit").forGetter(FluidIngredientAndAmount::unit)
        ).apply(instance, FluidIngredientAndAmount::new));

        public boolean isForNeoForge() {
            return unit.equals("millibuckets") || unit.equals("liters");
        };
    };
    
};
