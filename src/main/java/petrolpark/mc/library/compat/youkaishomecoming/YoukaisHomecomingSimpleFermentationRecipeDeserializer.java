package petrolpark.mc.library.compat.youkaishomecoming;

import java.util.Optional;

import com.mojang.serialization.Decoder;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.LiddedBasinRecipe;
import petrolpark.mc.library.core.data.recipe.compat.CompatRecipeDeserializer;
import petrolpark.mc.library.util.codec.CodecHelper;
import petrolpark.mc.library.util.codec.RecordDecoderBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@RequiresCreate
public class YoukaisHomecomingSimpleFermentationRecipeDeserializer implements CompatRecipeDeserializer<LiddedBasinRecipe> {

    public static final ResourceLocation SERIALIZER_ID = Mods.YOUKAIS_HOMECOMING.asResource("simple_fermenting");

    public static final Decoder<Optional<LiddedBasinRecipe>> DECODER = RecordDecoderBuilder.create(instance -> instance.group(
        RecordDecoderBuilder.of(Ingredient.CODEC.listOf().fieldOf("ingredients")),
        RecordDecoderBuilder.of(FluidStack.CODEC.fieldOf("inputFluid")),
        RecordDecoderBuilder.of(FluidStack.CODEC.fieldOf("outputFluid")),
        RecordDecoderBuilder.of(ItemStack.CODEC.listOf().fieldOf("results")),
        RecordDecoderBuilder.of(CodecHelper.POS_INT.fieldOf("time"))
    ).apply(instance, (ingredients, inputFluid, outputFluid, results, time) -> {
        final LiddedBasinRecipe.Builder builder = LiddedBasinRecipe.builder()
            .duration(time);
        ingredients.forEach(builder::require);
        results.forEach(builder::output);
        if (!inputFluid.isEmpty()) builder.require(SizedFluidIngredient.of(inputFluid));
        if (!outputFluid.isEmpty()) builder.output(outputFluid);
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
    
};
