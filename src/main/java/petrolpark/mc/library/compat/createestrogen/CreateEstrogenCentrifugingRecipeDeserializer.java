package petrolpark.mc.library.compat.createestrogen;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugationRecipe;
import petrolpark.mc.library.core.data.recipe.compat.CompatRecipeDeserializer;
import petrolpark.mc.library.util.codec.RecordDecoderBuilder;

@RequiresCreate
public class CreateEstrogenCentrifugingRecipeDeserializer implements CompatRecipeDeserializer<CentrifugationRecipe> {

    public static final ResourceLocation SERIALIZER_ID = Mods.CREATE_ESTROGEN.asResource("centrifuging");

    public static final Decoder<Optional<CentrifugationRecipe>> DECODER = RecordDecoderBuilder.create(instance -> instance.group(
        RecordDecoderBuilder.of(RatioFluid.CODEC.listOf().fieldOf("ingredients")),
        RecordDecoderBuilder.of(RatioFluid.CODEC.fieldOf("result"))
    ).apply(instance, (inputs, output) -> {
        if (inputs.size() != 1) return Optional.empty();
        return Optional.of(CentrifugationRecipe.builder()
            .require(SizedFluidIngredient.of(inputs.get(0).fluid(), 500 * inputs.get(0).amountPerTick()))
            .output(new FluidStack(output.fluid(), 500 * output.amountPerTick()))
            .duration(500)
            .build()
        );
    }));

    @Override
    public ResourceLocation serializerId() {
        return SERIALIZER_ID;
    };

    @Override
    public Decoder<Optional<CentrifugationRecipe>> decoder() {
        return DECODER;
    };

    @Override
    public ResourceLocation createId(ResourceLocation baseId) {
        return baseId.withPrefix(Petrolpark.MOD_ID + "/centrifugation/");
    };

    record RatioFluid(Fluid fluid, int amountPerTick) {

        public static final Codec<RatioFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(RatioFluid::fluid),
            Codec.INT.fieldOf("amount_per_tick").forGetter(RatioFluid::amountPerTick)
        ).apply(instance, RatioFluid::new));
    };
    
};
