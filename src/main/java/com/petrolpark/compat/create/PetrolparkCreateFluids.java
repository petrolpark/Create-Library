package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonFluidTag;
import static com.petrolpark.core.registrate.PetrolparkTagGen.tagFlowingFluidUnrequired;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.fluid.ColoredFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class PetrolparkCreateFluids {

    public static final FluidEntry<VirtualFluid>
    
    BLOOD = sharedColoredWaterFluid(SharedFeatureFlag.BLOOD, "blood", 0xFFD10000)
        .transform(tagFlowingFluidUnrequired(commonFluidTag("blood")))
        .register(),
    CREAM = sharedSingleTextureVirtualFluid(SharedFeatureFlag.MILK_PRODUCTS, "cream")
        .transform(tagFlowingFluidUnrequired(commonFluidTag("cream")))
        .register(),
    SUNFLOWER_OIL = sharedColoredWaterFluid(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil", 0x80EFE864)
        .transform(tagFlowingFluidUnrequired(commonFluidTag("oil"), commonFluidTag("oil/cooking"), commonFluidTag("oil/sunflower")))
        .register(),
    SKIMMED_MILK = sharedColoredWaterFluid(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk", 0x80FFFFFF)
        .transform(tagFlowingFluidUnrequired(Tags.Fluids.MILK, commonFluidTag("milk/skimmed")))
        .register();

    private static FluidBuilder<VirtualFluid, PetrolparkRegistrate> sharedColoredWaterFluid(SharedFeatureFlag featureFlag, String name, int color) {
        return sharedColoredFluid(featureFlag, name, color, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"));
    };

    private static final FluidBuilder<VirtualFluid, PetrolparkRegistrate> sharedColoredFluid(SharedFeatureFlag featureFlag, String name, int color, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return sharedVirtualFluid(featureFlag, name, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), VirtualFluid::createSource, VirtualFluid::createFlowing);
    };

    private static FluidBuilder<VirtualFluid, PetrolparkRegistrate> sharedSingleTextureVirtualFluid(SharedFeatureFlag featureFlag, String name) {
        return sharedVirtualFluid(featureFlag, name, Petrolpark.asResource("fluid/"+name), Petrolpark.asResource("fluid/"+name), CreateRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing);
    };

    public static final <T extends BaseFlowingFluid> FluidBuilder<T, PetrolparkRegistrate> sharedVirtualFluid(SharedFeatureFlag featureFlag, String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
		return REGISTRATE.sharedEntry(featureFlag, name, c -> new VirtualFluidBuilder<>(REGISTRATE, REGISTRATE, name, c, stillTexture, flowingTexture, typeFactory, sourceFactory, flowingFactory));
	};
    
    public static final void register() {};
};
