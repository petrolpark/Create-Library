package com.petrolpark.compat.brewinandchewin;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.petrolpark.Petrolpark;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.Mods;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.codec.RecordDecoderBuilder;
import com.petrolpark.core.recipe.compat.CompatRecipeDeserializer;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@RequiresCreate
public class BnCPouringRecipeDeserializer implements CompatRecipeDeserializer<FillingRecipe> {

    public static final ResourceLocation SERIALIZER_ID = Mods.BREWIN_AND_CHEWIN.asResource("keg_pouring");
    
    public static final Decoder<Optional<FillingRecipe>> DECODER = RecordDecoderBuilder.create(instance -> instance.group(
        RecordDecoderBuilder.of(FluidStack.CODEC.fieldOf("fluid")),
        RecordDecoderBuilder.of(ItemStack.CODEC.optionalFieldOf("container")),
        RecordDecoderBuilder.of(ItemStack.CODEC.fieldOf("output")),
        RecordDecoderBuilder.of(Codec.STRING.optionalFieldOf("unit")),
        RecordDecoderBuilder.of(Codec.BOOL.optionalFieldOf("strict", false))
    ).apply(instance, (fluid, containerOptional, filled, unit, strict) -> {
        if (!PetrolparkConfigs.common().brewinAndChewinFermentingInLiddedBasin.get()) return Optional.empty();

        if (unit.isPresent() && !unit.get().equals(BnCFluidIngredient.UNIT_LITERS) && !unit.get().equals(BnCFluidIngredient.UNIT_MILLIBUCKETS)) return Optional.empty();

        final StandardProcessingRecipe.Builder<FillingRecipe> builder = new StandardProcessingRecipe.Builder<>(FillingRecipe::new, Petrolpark.asResource("ishouldnotexist"))
            .require(SizedFluidIngredient.of(fluid));
        
        final ItemStack container = containerOptional.orElse(filled.getCraftingRemainingItem());
        if (container.isEmpty()) return Optional.empty();

        if (strict) return Optional.empty();
        builder.require(container.getItem());

        builder.output(filled);

        return Optional.of(builder.build());
    }));

    @Override
    public ResourceLocation serializerId() {
        return SERIALIZER_ID;
    };

    @Override
    public Decoder<Optional<FillingRecipe>> decoder() {
        return DECODER;
    };

    @Override
    public boolean shouldDeserialize(JsonElement element, ResourceLocation id) {
        return !id.getPath().startsWith("pouring/create/");
    };

    @Override
    public ResourceLocation createId(ResourceLocation baseId) {
        return baseId.withPrefix(Petrolpark.MOD_ID + "/filling/");
    };
};
