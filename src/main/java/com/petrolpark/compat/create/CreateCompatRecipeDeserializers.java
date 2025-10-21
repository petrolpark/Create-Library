package com.petrolpark.compat.create;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.brewinandchewin.BnCFermentingRecipeDeserializer;
import com.petrolpark.compat.brewinandchewin.BnCPouringRecipeDeserializer;

public class CreateCompatRecipeDeserializers {

    public static final BnCFermentingRecipeDeserializer BNC_FERMENTING = Petrolpark.COMPAT_RECIPES.register(new BnCFermentingRecipeDeserializer());
    public static final BnCPouringRecipeDeserializer BNC_POURING = Petrolpark.COMPAT_RECIPES.register(new BnCPouringRecipeDeserializer());
  
    public static void register() {};
};
