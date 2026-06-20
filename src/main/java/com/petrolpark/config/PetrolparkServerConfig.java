package com.petrolpark.config;

import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.RequiresCreate;
import com.simibubi.create.api.stress.BlockStressValues;

import net.createmod.catnip.config.ConfigBase;

public class PetrolparkServerConfig extends ConfigBase {

    public PetrolparkServerConfig() {
        Mods.CREATE.executeIfInstalled(() -> this::createConfigs);
    };

    public final ConfigBool syncChiseledBookshelves = b(true, "syncChiseledBookshelves", "Chiseled Bookshelves broadcast their data to clients");

    // Extended Inventory
    public final ConfigGroup extendedInventory = group(0, "extendedInventory");
        public final ConfigBool extendedInventorySafeMode = b(true, "safeMode", "Only attempt to attach the Extended Inventory to menus known not to cause problems");

    // Processing
    public final ConfigGroup processing = group(0, "processing");
        public final ConfigBool ageingInVanillaBarrels = b(true, "ageingInVanillaBarrels", "Ageing Recipes are possible in Vanilla Barrels (and modded Barrels which extend it)");
        public final ConfigBool chiseledBookShelfProvidesRecipeBooks = b(true, "chiseledBookShelfProvidesRecipeBooks", "Chiseled Bookshelves containing Recipe Books and Knowledge Books can provide the Recipes they contain to adjacent Blocks");

    // Flags
    public final ConfigGroup flags = group(0, "flags");
        public final ConfigBool shapedCraftingPropagatesFlags = b(true, "propagateShapedCrafting", "Simple shaped Crafting Recipes will propagate the inputs' Flags to the outputs, regardless of what they are crafted in");
        public final ConfigBool shapelessCraftingPropagatesFlags = b(true, "propagateShapelessCrafting", "Simple shapeless Crafting Recipes will propagate the inputs' Flags to the output, regardless of what they are crafted in");
        public final ConfigBool craftingTablePropagatesFlags = b(true, "propagateCraftingTable", "Recipes done in Crafting Tables and the Inventory will propagate the inputs' Flags to the the output", "This can include simple shaped and shapeless Crafting Recipes, as well as custom ones like crafting Firework Stars");
        public final ConfigBool cookingPropagatesFlags = b(true, "propagateCooking", "Smelting, Blasting, Smoking etc. Recipes will propagate the Flags of the input to the output");
        public final ConfigBool brewingPropagatesFlags = b(true, "propagateBrewing", "Brewing will propagate the Flags of inputs to the resultant Potion");
        public final ConfigBool brewingWaterBottleFlagsIgnored = b(true, "brewingIgnoreWaterBottle", "The Flags of a Potion brewed from a Water Bottle depend only on the Flags of the added ingredient, not the Water Bottle");
        public final ConfigBool smithingPropagatesFlags = b(true, "propagateSmithing", "Smithing will propagate the Flags of the base Item and added Item to the result");

    // Compat
    public final ConfigGroup compatibility = group(0, "compatibility");
        // Create
        public final ConfigGroup create = group(1, "create");
            public final ConfigBool createEncasedCrushingWheels = b(true, "encasedCrushingWheels", "Crushing Wheels can be encased in Brass Casing, allowing a Recipe filter to be set");
            public final ConfigBool createArmsTargetChainConveyors = b(false, "armsTargetChainConveyors", "[Must be enabled by a dependent]", "Whether Mechanical Arms can take from and place on Chain Conveyors");
            public final ConfigBool createChainConveyorDrying = b(true, "chainConveyorDrying", "Whether Drying Recipes can be done on Chain Conveyors");
            public final ConfigGroup centrifuge = group(2, "centrifuge");
                public final ConfigInt centrifugeTankCapacity = i(2000, 0, Integer.MAX_VALUE, "Capacity of each Centrifuge tank");
                public final ConfigBool potionCentrifugation = b(true, "Centrifuges can separate Potions into their Ingredients");
                public final ConfigBool centrifugePropagatesFlags = b(true, "propagateFlags", "Centrifugation Recipes will propagate the Flags of the inputs to the outputs");
            public final ConfigGroup createFlags = group(2, "flags");
                public final ConfigFloat createFluidFlagWeight = f(100f, 0f, Float.MAX_VALUE, "fluidWeight", "How many mB of Fluid should be considered to be equal to one Item when weighting preserved Flags in any Recipes involving Fluids", "Set to 0 to not count the Flags of input Fluids. Flags will still propagate to output Fluids.");
                public final ConfigBool createBasinRecipesPropagateFlags = b(true, "propagateBasin", "Recipes done in a Basin will propagate the Flags of the input to the outputs");
                public final ConfigBool createCrushingRecipesPropagateFlags = b(true, "propagateCrushing", "Recipes done by Millstones and Crushing Wheels will propagate the Flags of the input to the outputs");
                public final ConfigBool createSandingRecipesPropagateFlags = b(true, "propagateSanding", "Manual Sandpaper Polishing Recipes will propagate the Flags of the input to the output");
                public final ConfigBool createCuttingRecipesPropagateFlags = b(true, "propagateCutting", "Cutting Recipes will propagate the Flags of the input to the output");
                public final ConfigBool createOtherRecipesPropagateFlags = b(true, "propagateOther", "Pressing, Deploying, Washing and all Mechanical Fan Recipes propagate the Flags of the input to the outputs");
            public final ConfigGroup redstoneProgrammer = group(2, "redstoneProgrammer");
                public final ConfigInt redstoneProgrammerMaxChannels = i(20, 1, 256, "maxChannels", "Maximum number of channels in a single Redstone Programmer");
                public final ConfigInt redstoneProgrammerMinTicksPerBeat = i(2, 1, 20, "minTicksPerBeat", "The shortest length (in ticks) a Redstone Programmer can change signal over.", "The lower this is, the greater the potential for players to cause lag.");

    @Override
    public String getName() {
        return "Server";
    }

    @RequiresCreate
    private final void createConfigs() {
        final PetrolparkStressConfig stress = nested(0, PetrolparkStressConfig::new);
		BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
		BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
    };
    
};
