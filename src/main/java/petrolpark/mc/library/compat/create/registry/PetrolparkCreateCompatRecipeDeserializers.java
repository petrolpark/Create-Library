package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.Petrolpark.COMPAT_RECIPES;

import petrolpark.mc.library.compat.bitterballen.BitterballenDeepFryingRecipeDeserializer;
import petrolpark.mc.library.compat.brewinandchewin.BnCFermentingRecipeDeserializer;
import petrolpark.mc.library.compat.brewinandchewin.BnCPouringRecipeDeserializer;
import petrolpark.mc.library.compat.createestrogen.CreateEstrogenCentrifugingRecipeDeserializer;
import petrolpark.mc.library.compat.dieselGenerators.CDGBasinFermentingRecipeDeserializer;
import petrolpark.mc.library.compat.youkaishomecoming.YoukaisHomecomingSimpleFermentationRecipeDeserializer;

public class PetrolparkCreateCompatRecipeDeserializers {

    public static final BitterballenDeepFryingRecipeDeserializer BITTERBALLEN_DEEP_FRYING = COMPAT_RECIPES.register(new BitterballenDeepFryingRecipeDeserializer());

    public static final BnCFermentingRecipeDeserializer BNC_FERMENTING = COMPAT_RECIPES.register(new BnCFermentingRecipeDeserializer());
    public static final BnCPouringRecipeDeserializer BNC_POURING = COMPAT_RECIPES.register(new BnCPouringRecipeDeserializer());
    
    public static final CDGBasinFermentingRecipeDeserializer CDG_BASIN_FERMENTING = COMPAT_RECIPES.register(new CDGBasinFermentingRecipeDeserializer());

    public static final CreateEstrogenCentrifugingRecipeDeserializer CREATE_ESTROGEN_CENTRIFUGING = COMPAT_RECIPES.register(new CreateEstrogenCentrifugingRecipeDeserializer());

    public static final YoukaisHomecomingSimpleFermentationRecipeDeserializer YOUKAIS_HOMECOMING_SIMPLE_FERMENTATION = COMPAT_RECIPES.register(new YoukaisHomecomingSimpleFermentationRecipeDeserializer());
  
    public static final void register() {};
};
