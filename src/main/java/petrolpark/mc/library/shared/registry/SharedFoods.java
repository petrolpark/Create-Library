package petrolpark.mc.library.shared.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class SharedFoods {
  
    public static final FoodProperties

    BUTTER = new FoodProperties.Builder().nutrition(6).saturationModifier(0.1f).build(),
    CREAM = new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).build(),
    EGG_WHITE = new FoodProperties.Builder().nutrition(3).saturationModifier(0.2f).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200), 0.25f).build(),
    FRIES = new FoodProperties.Builder().nutrition(6).saturationModifier(1.5f).build(),
    MASHED_POTATO = new FoodProperties.Builder().nutrition(5).saturationModifier(1.4f).build(),
    OIL = new FoodProperties.Builder().nutrition(6).saturationModifier(0.5f).effect(SharedMobEffects.SLIPPING.asInstanceSupplier(1200, 0), 1f).build(),
    RAW_FRIES = new FoodProperties.Builder().nutrition(2).saturationModifier(0.6f).build(),
    YOLK = new FoodProperties.Builder().nutrition(1).saturationModifier(0.2f).fast().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200), 0.25f).build();
};
