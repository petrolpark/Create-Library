package petrolpark.mc.library.compat.dieselGenerators;

import java.util.Optional;

import com.mojang.serialization.Decoder;

import net.minecraft.resources.ResourceLocation;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.core.data.recipe.RecordProcessingRecipeParams;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.LiddedBasinRecipe;
import petrolpark.mc.library.core.data.recipe.compat.CompatRecipeDeserializer;

public class CDGBasinFermentingRecipeDeserializer implements CompatRecipeDeserializer<LiddedBasinRecipe> {

    public static final ResourceLocation SERIALIZER_ID = Mods.CREATE_DIESEL_GENERATORS.asResource("basin_fermenting");

    public static final Decoder<Optional<LiddedBasinRecipe>> DECODER = RecordProcessingRecipeParams.DECODER.map(params -> {
        return Optional.of(params.addToBuilder(LiddedBasinRecipe.builder()).withBubbles().build());
    });

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
        return baseId.withPrefix(Petrolpark.MOD_ID + "/lidded_basin/");
    };
    
};
