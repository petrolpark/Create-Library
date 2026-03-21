package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonFluidTag;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.fluid.ColoredFluidType;
import com.petrolpark.core.world.fluid.VirtualFluidWithContainer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class PetrolparkCreateFluids {

    public static final FluidEntry<VirtualFluidWithContainer>
    
    BLOOD = sharedColoredWaterContainerFluid(SharedFeatureFlag.BLOOD, "blood", 0xFFD10000, PetrolparkCreateItems.BLOOD_BUCKET)
        .tag(commonFluidTag("blood"))
        .register(),
    CREAM = sharedSingleTextureVirtualContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "cream", PetrolparkCreateItems.CREAM_BUCKET)
        .tag(commonFluidTag("cream"))
        .register(),
    SKIMMED_MILK = sharedColoredWaterContainerFluid(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk", 0xFFFFFFFF, PetrolparkCreateItems.SKIMMED_MILK_BUCKET)
        .tag(Tags.Fluids.MILK, commonFluidTag("milk/skimmed"))
        .register(),

    SUNFLOWER_OIL = sharedColoredWaterContainerFluid(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil", 0x80EFE864, PetrolparkCreateItems.SUNFLOWER_OIL_BOTTLE)
        .tag(commonFluidTag("oil"), PetrolparkTags.Fluids.COOKING_OILS, commonFluidTag("oil/sunflower"))
        .register();

    // private static FluidBuilder<VirtualFluid, PetrolparkRegistrate> sharedColoredWaterFluid(SharedFeatureFlag featureFlag, String name, int color) {
    //     return sharedColoredFluid(featureFlag, name, color, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"));
    // };

    private static final FluidBuilder<VirtualFluidWithContainer, PetrolparkRegistrate> sharedColoredWaterContainerFluid(SharedFeatureFlag featureFlag, String name, int color, ItemLike bucket) {
        return sharedColoredContainerFluid(featureFlag, name, color, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"), bucket);
    };

    // private static final FluidBuilder<VirtualFluid, PetrolparkRegistrate> sharedColoredFluid(SharedFeatureFlag featureFlag, String name, int color, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
    //     return sharedVirtualFluid(featureFlag, name, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), VirtualFluid::createSource, VirtualFluid::createFlowing);
    // };

    private static final FluidBuilder<VirtualFluidWithContainer, PetrolparkRegistrate> sharedColoredContainerFluid(SharedFeatureFlag featureFlag, String name, int color, ResourceLocation stillTexture, ResourceLocation flowingTexture, ItemLike bucket) {
        return sharedVirtualFluid(featureFlag, name, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), p -> VirtualFluidWithContainer.createSource(p, bucket), p -> VirtualFluidWithContainer.createFlowing(p, bucket));
    };

    private static FluidBuilder<VirtualFluidWithContainer, PetrolparkRegistrate> sharedSingleTextureVirtualContainerFluid(SharedFeatureFlag featureFlag, String name, ItemLike bucket) {
        return sharedVirtualFluid(featureFlag, name, Petrolpark.asResource("fluid/"+name), Petrolpark.asResource("fluid/"+name), CreateRegistrate::defaultFluidType, p -> VirtualFluidWithContainer.createSource(p, bucket), p -> VirtualFluidWithContainer.createFlowing(p, bucket));
    };

    public static final <T extends BaseFlowingFluid> FluidBuilder<T, PetrolparkRegistrate> sharedVirtualFluid(SharedFeatureFlag featureFlag, String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
		return REGISTRATE.sharedEntry(featureFlag, name, c -> new VirtualFluidBuilder<>(REGISTRATE, REGISTRATE, name, c, stillTexture, flowingTexture, typeFactory, sourceFactory, flowingFactory)).asOptional();
	};
    
    public static final void register() {};
};
