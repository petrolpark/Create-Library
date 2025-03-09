package com.petrolpark;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Petrolpark.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PetrolparkConfig {

    public static class Server {

        // Contaminants
        public final BooleanValue shapedCraftingPropagatesContaminants;
        public final BooleanValue shapelessCraftingPropagatesContaminants;
        public final BooleanValue craftingTablePropagatesContaminants;
        public final BooleanValue cookingPropagatesContaminants;
        public final BooleanValue brewingPropagatesContaminants;
        public final BooleanValue brewingWaterBottleContaminantsIgnored;
        public final BooleanValue smithingPropagatesContaminants;

        // Create
        public final DoubleValue createFluidContaminantWeight;
        public final BooleanValue createBasinRecipesPropagateContaminants;
        public final BooleanValue createCrushingRecipesPropagateContaminants;
        public final BooleanValue createSandingRecipesPropagateContaminants;
        public final BooleanValue createCuttingRecipesPropagateContaminants;
        public final BooleanValue createOtherRecipesPropagateContaminants;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Pquality world-specific Configs")
                   .push("server");

            builder.push("contamination"); {

                shapedCraftingPropagatesContaminants = builder
                    .comment("Simple shaped Crafting Recipes will propagate the inputs' Contaminants to the outputs, regardless of what they are crafted in")
                    .worldRestart()
                    .define("shapedCraftingPropagatesContaminants", true);

                shapelessCraftingPropagatesContaminants = builder
                    .comment("Simple shapeless Crafting Recipes will propagate the inputs' Contaminants to the output, regardless of what they are crafted in")
                    .worldRestart()
                    .define("shapelessCraftingPropagatesContaminants", true);

                craftingTablePropagatesContaminants = builder
                    .comment(
                        "Recipes done in Crafting Tables and the Inventory will propagate the inputs' Contaminants to the the output",
                        "This can include simple shaped and shapeless Crafting Recipes, as well as custom ones like crafting Firework Stars"
                    ).worldRestart()
                    .define("craftingTablePropagatesContaminants", true);

                cookingPropagatesContaminants = builder
                    .comment("Smelting, Blasting, Smoking etc. Recipes will propagate the Contaminants of the input to the output")
                    .worldRestart()
                    .define("cookingPropagatesContaminants", true);

                brewingPropagatesContaminants = builder
                    .comment("Brewing will propagate the Contaminants of inputs to the resultant Potion")
                    .worldRestart()
                    .define("brewingPropagatesContaminants", true);

                brewingWaterBottleContaminantsIgnored = builder
                    .comment("The Contaminants of a Potion brewed from a Water Bottle depend only on the Contaminants of the added ingredient, not the Water Bottle")
                    .worldRestart()
                    .define("brewingWaterBottleContaminantsIgnored", true);

                smithingPropagatesContaminants = builder
                    .comment("Smithing will propagate the Contaminants of the base Item and added Item to the result")
                    .worldRestart()
                    .define("smithingPropagatesContaminants", true);

            }; builder.pop();

            builder.push("compat"); {

                builder.push("create"); {

                    createFluidContaminantWeight = builder
                        .comment(
                            "How many mB of Fluid should be considered to be equal to one Item when weighting preserved Contaminants in any Recipes involving Fluids",
                            "Set to 0 to not count the contaminants of fluids. Contaminants will still propagate to resultant Fluids."
                        ).worldRestart()
                        .defineInRange("fluidContaminantWeight", 100d, 0d, Double.MAX_VALUE);

                    createBasinRecipesPropagateContaminants = builder
                        .comment("Recipes done in a Basin will propagate the Contaminants of the input to the outputs")
                        .worldRestart()
                        .define("basinRecipesPropagateContaminants", true);

                    createCrushingRecipesPropagateContaminants = builder
                        .comment("Recipes done by Millstones and Crushing Wheels will propagate the Contaminants of the input to the outputs")
                        .worldRestart()
                        .define("crushingRecipesPropagateContaminants", true);

                    createSandingRecipesPropagateContaminants = builder
                        .comment("Manual Sandpaper Polishing Recipes will propagate the Contaminants of the input to the output")
                        .worldRestart()
                        .define("sandingRecipesPropagateContaminants", true);

                    createCuttingRecipesPropagateContaminants = builder
                        .comment("Cutting Recipes will propagate the Contaminants of the input to the output")
                        .worldRestart()
                        .define("cuttingRecipesPropagateContaminants", true);

                    createOtherRecipesPropagateContaminants = builder
                        .comment("Pressing, Deploying, Washing and all Mechanical Fan Recipes propagate the Contaminants of the input to the outputs")
                        .worldRestart()
                        .define("otherRecipesPropagateContaminants", true);
                };

            }; builder.pop();

            builder.pop();
        };
    };

    protected static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    };
};

