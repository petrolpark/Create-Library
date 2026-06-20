package com.petrolpark.compat.create.registry;

import static com.petrolpark.Petrolpark.COMPAT_RECIPES;

import com.petrolpark.compat.brewinandchewin.BnCFermentingRecipeDeserializer;
import com.petrolpark.compat.brewinandchewin.BnCPouringRecipeDeserializer;
import com.petrolpark.compat.youkaishomecoming.YoukaisHomecomingSimpleFermentationRecipeDeserializer;

public class PetrolparkCreateCompatRecipeDeserializers {

    public static final BnCFermentingRecipeDeserializer BNC_FERMENTING = COMPAT_RECIPES.register(new BnCFermentingRecipeDeserializer());
    public static final BnCPouringRecipeDeserializer BNC_POURING = COMPAT_RECIPES.register(new BnCPouringRecipeDeserializer());
    
    public static final YoukaisHomecomingSimpleFermentationRecipeDeserializer YOUKAIS_HOMECOMING_SIMPLE_FERMENTATION = COMPAT_RECIPES.register(new YoukaisHomecomingSimpleFermentationRecipeDeserializer());
  
    public static final void register() {};
};
