package com.petrolpark.compat.brewinandchewin;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.petrolpark.Petrolpark;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.common.processing.basinlid.LiddedBasinRecipe;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.codec.RecordDecoderBuilder;
import com.petrolpark.core.recipe.compat.CompatRecipeDeserializer;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

@RequiresCreate
public class BnCFermentingRecipeDeserializer implements CompatRecipeDeserializer<LiddedBasinRecipe> {

    public static final ResourceLocation SERIALIZER_ID = Mods.BREWIN_AND_CHEWIN.asResource("fermenting");

    public static final Decoder<Optional<LiddedBasinRecipe>> DECODER = RecordDecoderBuilder.create(instance -> instance.group(
        RecordDecoderBuilder.of(Ingredient.CODEC.listOf(1, 4).fieldOf("ingredients")),
        RecordDecoderBuilder.ofOptional(BnCFluidIngredient.DECODER, "base_fluid"),
        RecordDecoderBuilder.of(Codec.either(FluidStack.CODEC, ItemStack.CODEC).fieldOf("result")),
        RecordDecoderBuilder.of(Codec.INT.optionalFieldOf("fermenting_time", 9600)),
        RecordDecoderBuilder.of(Codec.INT.optionalFieldOf("temperature", 3))
    ).apply(instance, (ingredients, bncFluidOptional, result, time, temperature) -> {
        if (!PetrolparkConfigs.common().brewinAndChewinFermentingInLiddedBasin.get()) return Optional.empty();

        final LiddedBasinRecipe.Builder builder = LiddedBasinRecipe.builder()
            .duration(time)
            .withBubbles();

        // Fluid input
        if (bncFluidOptional.isPresent()) {
            final Optional<com.simibubi.create.foundation.fluid.FluidIngredient> createFluidOptional = bncFluidOptional.get().asCreateIngredient();
            if (createFluidOptional.isEmpty()) return Optional.empty();
            else builder.require(createFluidOptional.get());
        };

        // Item inputs
        for (Ingredient ingredient : ingredients) {
            if (!ingredient.isEmpty()) builder.require(ingredient);
        };

        // Temperature
        switch (temperature) {
            case 3:
                break;
            case 4:
                builder.requiresHeat(HeatCondition.HEATED);
                break;
            case 5:
                builder.requiresHeat(HeatCondition.SUPERHEATED);
                break;
            default:
                return Optional.empty();
        };

        // Output
        result.map(builder::output, builder::output);

        return Optional.of(builder.build());
    }));

    @Override
    public ResourceLocation serializerId() {
        return SERIALIZER_ID;
    };

    @Override
    public Decoder<Optional<LiddedBasinRecipe>> decoder() {
        return DECODER;
    };

    @Override
    public ResourceLocation createId(ResourceLocation baseId) {
        return baseId.withPrefix(Petrolpark.MOD_ID+"/lidded_basin/");
    };

    @Override
    public String toString() {
        return "BnC Fermenting -> petrolpark Lidded Basin";
    };
    
};
