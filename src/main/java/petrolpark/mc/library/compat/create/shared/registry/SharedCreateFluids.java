package petrolpark.mc.library.compat.create.shared.registry;

import static petrolpark.mc.library.PetrolparkTags.commonFluidTag;
import static petrolpark.mc.library.compat.create.PetrolparkCreate.REGISTRATE;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.core.world.fluid.VirtualFluidWithContainer;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.neoforged.neoforge.common.Tags;

public class SharedCreateFluids {

    public static final FluidEntry<VirtualFluidWithContainer>
    
    BLOOD = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.BLOOD, "blood", 0xFFD10000, SharedCreateItems.BLOOD_BUCKET)
        .tag(commonFluidTag("blood"))
        .register(),
    CREAM = REGISTRATE.sharedContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "cream", Petrolpark.asResource("block/shared/cream"), Petrolpark.asResource("block/shared/cream"), SharedCreateItems.CREAM_BUCKET)
        .tag(commonFluidTag("cream"))
        .register(),
    EGG_WHITE = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.EGG_PRODUCTS, "egg_white", 0xFFf3ff4f, SharedCreateItems.EGG_WHITE_BOTTLE)
        .register(),
    SKIMMED_MILK = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk", 0xFFFFFFFF, SharedCreateItems.SKIMMED_MILK_BUCKET)
        .tag(Tags.Fluids.MILK, commonFluidTag("milk/skimmed"))
        .register(),
    SUNFLOWER_OIL = REGISTRATE.sharedColoredWaterContainerFluid(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil", 0x80EFE864, SharedCreateItems.SUNFLOWER_OIL_BOTTLE)
        .tag(commonFluidTag("oil"), PetrolparkTags.Fluids.COOKING_OILS, commonFluidTag("oil/sunflower"))
        .register();
    
    public static final void register() {};
};
