package petrolpark.mc.library.compat.bitterballen;

import java.util.Optional;

import com.mojang.serialization.Decoder;

import net.minecraft.resources.ResourceLocation;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.BoilingRecipe;
import petrolpark.mc.library.core.data.recipe.compat.CompatRecipeDeserializer;

@RequiresCreate
public class BitterballenDeepFryingRecipeDeserializer implements CompatRecipeDeserializer<BoilingRecipe> {
    
    public static final ResourceLocation SERIALIZER_ID = Mods.CREATE_BITTERBALLEN.asResource("deep_frying");

    public static final Decoder<Optional<BoilingRecipe>> DECODER = BoilingRecipe.CODEC.xmap(Optional::of, Optional::get).decoder();

    @Override
    public ResourceLocation serializerId() {
        return SERIALIZER_ID;
    };

    @Override
    public Decoder<Optional<BoilingRecipe>> decoder() {
        return DECODER;
    };

    @Override
    public ResourceLocation createId(ResourceLocation baseId) {
        return baseId.withPrefix(Petrolpark.MOD_ID + "/boiling/");
    };
    
};
