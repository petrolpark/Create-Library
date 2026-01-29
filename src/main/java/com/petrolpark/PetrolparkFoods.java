package com.petrolpark;

import net.minecraft.world.food.FoodProperties;

public class PetrolparkFoods {
  
    public static final FoodProperties

    BUTTER = new FoodProperties.Builder().nutrition(6).saturationModifier(0.1f).build(),
    MASHED_POTATO = new FoodProperties.Builder().nutrition(5).saturationModifier(1.4f).build();
};
