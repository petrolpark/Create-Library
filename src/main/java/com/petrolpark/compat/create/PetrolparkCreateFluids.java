package com.petrolpark.compat.create;

import static com.petrolpark.PetrolparkTags.commonFluidTag;
import static com.petrolpark.compat.create.PetrolparkCreate.REGISTRATE;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.world.fluid.VirtualFluidWithContainer;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.neoforged.neoforge.common.Tags;

public class PetrolparkCreateFluids {

    public static final FluidEntry<VirtualFluidWithContainer>
    
    BLOOD = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.BLOOD, "blood", 0xFFD10000, PetrolparkCreateItems.BLOOD_BUCKET)
        .tag(commonFluidTag("blood"))
        .register(),
    CREAM = REGISTRATE.sharedSingleTextureVirtualContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "cream", PetrolparkCreateItems.CREAM_BUCKET)
        .tag(commonFluidTag("cream"))
        .register(),
    SKIMMED_MILK = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk", 0xFFFFFFFF, PetrolparkCreateItems.SKIMMED_MILK_BUCKET)
        .tag(Tags.Fluids.MILK, commonFluidTag("milk/skimmed"))
        .register(),

    SUNFLOWER_OIL = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil", 0x80EFE864, PetrolparkCreateItems.SUNFLOWER_OIL_BOTTLE)
        .tag(commonFluidTag("oil"), PetrolparkTags.Fluids.COOKING_OILS, commonFluidTag("oil/sunflower"))
        .register();
    
    public static final void register() {};
};
