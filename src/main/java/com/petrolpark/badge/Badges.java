package com.petrolpark.badge;

import static com.petrolpark.Petrolpark.DESTROY_REGISTRATE;
import static com.petrolpark.Petrolpark.REGISTRATE;

import java.util.function.Supplier;

import com.petrolpark.PetrolparkTags;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.Ingredient;

public class Badges {

    private static final Supplier<Ingredient> GOLD_SHEET_INGREDIENT = () -> Ingredient.of(PetrolparkTags.forgeItemTag("plates/gold"));

    public static final RegistryEntry<Badge>

    BETA_TESTER = REGISTRATE.get().badge("beta_tester")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    BESTIE = REGISTRATE.get().badge("bestie")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    COMPETITION_WINNER = REGISTRATE.get().badge("competition_winner")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    CONTENT_CREATOR = REGISTRATE.get().badge("content_creator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    DEVELOPER = REGISTRATE.get().badge("developer")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    EARLY_BIRD = REGISTRATE.get().badge("early_bird")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    NITRO = REGISTRATE.get().badge("nitro")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    SUGGESTION = REGISTRATE.get().badge("suggestion")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    TRANSLATOR = REGISTRATE.get().badge("translator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),

    PATREON_1 = DESTROY_REGISTRATE.get().badge("patreon_1")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_2 = DESTROY_REGISTRATE.get().badge("patreon_2")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_3 = DESTROY_REGISTRATE.get().badge("patreon_3")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register();

    public static void register() {};
};
