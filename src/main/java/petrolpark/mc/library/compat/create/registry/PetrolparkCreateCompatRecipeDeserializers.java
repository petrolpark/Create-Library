package petrolpark.mc.library.compat.create.registry;

import static petrolpark.mc.library.Petrolpark.COMPAT_RECIPES;

import petrolpark.mc.library.compat.brewinandchewin.BnCFermentingRecipeDeserializer;
import petrolpark.mc.library.compat.brewinandchewin.BnCPouringRecipeDeserializer;
import petrolpark.mc.library.compat.youkaishomecoming.YoukaisHomecomingSimpleFermentationRecipeDeserializer;

public class PetrolparkCreateCompatRecipeDeserializers {

    public static final BnCFermentingRecipeDeserializer BNC_FERMENTING = COMPAT_RECIPES.register(new BnCFermentingRecipeDeserializer());
    public static final BnCPouringRecipeDeserializer BNC_POURING = COMPAT_RECIPES.register(new BnCPouringRecipeDeserializer());
    
    public static final YoukaisHomecomingSimpleFermentationRecipeDeserializer YOUKAIS_HOMECOMING_SIMPLE_FERMENTATION = COMPAT_RECIPES.register(new YoukaisHomecomingSimpleFermentationRecipeDeserializer());
  
    public static final void register() {};
};
