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

    BETA_TESTER = REGISTRATE.badge("beta_tester")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    BESTIE = REGISTRATE.badge("bestie")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    COMPETITION_WINNER = REGISTRATE.badge("competition_winner")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    CONTENT_CREATOR = REGISTRATE.badge("content_creator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    DEVELOPER = REGISTRATE.badge("developer")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    EARLY_BIRD = REGISTRATE.badge("early_bird")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    NITRO = REGISTRATE.badge("nitro")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    SUGGESTION = REGISTRATE.badge("suggestion")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    TRANSLATOR = REGISTRATE.badge("translator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),

    PATREON_1 = DESTROY_REGISTRATE.badge("patreon_1")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_2 = DESTROY_REGISTRATE.badge("patreon_2")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_3 = DESTROY_REGISTRATE.badge("patreon_3")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register();

    public static void register() {};
};
