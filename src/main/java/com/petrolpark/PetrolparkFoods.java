package com.petrolpark;

import net.minecraft.world.food.FoodProperties;

public class PetrolparkFoods {
  
    public static final FoodProperties

    BUTTER = new FoodProperties.Builder().nutrition(6).saturationModifier(0.1f).build(),
    FRIES = new FoodProperties.Builder().nutrition(6).saturationModifier(1.5f).build(),
    MASHED_POTATO = new FoodProperties.Builder().nutrition(5).saturationModifier(1.4f).build(),
    RAW_FRIES = new FoodProperties.Builder().nutrition(2).saturationModifier(0.6f).build();
};
