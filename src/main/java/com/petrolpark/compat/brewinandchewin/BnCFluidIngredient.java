package com.petrolpark.compat.brewinandchewin;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.petrolpark.RequiresCreate;
import com.petrolpark.core.codec.EitherDecoder;
import com.petrolpark.core.codec.RecordDecoderBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SingleFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

@RequiresCreate
public record BnCFluidIngredient(FluidIngredient ingredient, int amount, String unit) {

    public static final String UNIT_LITERS = "liters";
    public static final String UNIT_MILLIBUCKETS = "millibuckets";

    public static final Decoder<BnCFluidIngredient> DECODER = RecordDecoderBuilder.create(instance -> instance.group(
        RecordDecoderBuilder.of(EitherDecoder.withAlternative(FluidIngredient.CODEC, EitherDecoder.<FluidIngredient>withAlternative(
            RecordDecoderBuilder.create(ingredientInstance -> ingredientInstance.group(
                RecordDecoderBuilder.of(RegistryCodecs.homogeneousList(Registries.FLUID).flatMap(set -> set instanceof HolderSet.Named<Fluid> tagSet ? DataResult.success(tagSet.key()) : DataResult.error(() -> "Only supports Tags defined in their own files")).fieldOf("tag")),
                componentDecoder()
            ).apply(ingredientInstance, (tag, components) -> new TagFluidIngredient(tag))),
            RecordDecoderBuilder.create(ingredientInstance -> ingredientInstance.group(
                RecordDecoderBuilder.of(FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id")),
                componentDecoder()
            ).apply(ingredientInstance, (fluid, components) -> new SingleFluidIngredient(fluid)))
        )).fieldOf("ingredient")),
        RecordDecoderBuilder.of(Codec.INT.fieldOf("amount")),
        RecordDecoderBuilder.of(Codec.STRING.fieldOf("unit"))
    ).apply(instance, BnCFluidIngredient::new));

    private static final <O> RecordDecoderBuilder<O, DataComponentPatch> componentDecoder() {
        return RecordDecoderBuilder.ofOptional(DataComponentPatch.CODEC.<DataComponentPatch>flatMap(components -> components.isEmpty() ? DataResult.success(components) : DataResult.error(() -> "Specific Components not supported in recipe")), "components", DataComponentPatch.EMPTY);
    };

    public Optional<SizedFluidIngredient> asNeoIngredient() {
        if (!unit.equals(UNIT_MILLIBUCKETS) && !unit.equals(UNIT_LITERS)) return Optional.empty();
        return Optional.of(new SizedFluidIngredient(ingredient(), amount()));
    };
};
