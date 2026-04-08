package com.petrolpark.compat.create.core;

import com.petrolpark.AbstractPetrolparkRegistrate;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.fluid.ColoredFluidType;
import com.petrolpark.core.registrate.builder.SharedCreateBlockEntityBuilder;
import com.petrolpark.core.world.fluid.VirtualFluidWithContainer;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class AbstractPetrolparkCreateRegistrate<R extends AbstractPetrolparkCreateRegistrate<R>> extends AbstractPetrolparkRegistrate<R> {

    protected AbstractPetrolparkCreateRegistrate(String modid) {
        super(modid);
    };

    public <T extends BlockEntity> SharedCreateBlockEntityBuilder<T, R> sharedCreateBlockEntity(SharedFeatureFlag featureFlag, String name, BlockEntityFactory<T> factory) {
        return (SharedCreateBlockEntityBuilder<T, R>)sharedEntry(featureFlag, name, callback -> SharedCreateBlockEntityBuilder.create(self(), self(), featureFlag, name, callback, factory));
    };

    public FluidBuilder<VirtualFluidWithContainer, R> coloredWaterFluid(String name, int color, ItemLike container) {
        return coloredFluid(name, color, container, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"));
    };

    public FluidBuilder<VirtualFluidWithContainer, R> coloredFluid(String name, int color, ItemLike container, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), p -> VirtualFluidWithContainer.createSource(p, container), p -> VirtualFluidWithContainer.createFlowing(p, container)));
    };

    public FluidBuilder<VirtualFluid, R> virtualFluid(String name) {
        return entry(name, c -> new VirtualFluidBuilder<>(self(), self(), name, c,
			ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_still"), ResourceLocation.fromNamespaceAndPath(getModid(), "fluid/" + name + "_flow"),
			CreateRegistrate::defaultFluidType, VirtualFluid::createSource, VirtualFluid::createFlowing)
        );
    };

    public FluidBuilder<VirtualFluid, R> sharedColoredWaterFluid(SharedFeatureFlag featureFlag, String name, int color) {
        return sharedColoredFluid(featureFlag, name, color, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"));
    };

    public FluidBuilder<VirtualFluidWithContainer, R> sharedColoredWaterContainerFluid(SharedFeatureFlag featureFlag, String name, int color, ItemLike bucket) {
        return sharedColoredContainerFluid(featureFlag, name, color, ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still"), ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow"), bucket);
    };

    public FluidBuilder<VirtualFluid, R> sharedColoredFluid(SharedFeatureFlag featureFlag, String name, int color, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return sharedVirtualFluid(featureFlag, name, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), VirtualFluid::createSource, VirtualFluid::createFlowing);
    };

    public FluidBuilder<VirtualFluidWithContainer, R> sharedColoredContainerFluid(SharedFeatureFlag featureFlag, String name, int color, ResourceLocation stillTexture, ResourceLocation flowingTexture, ItemLike bucket) {
        return sharedVirtualFluid(featureFlag, name, stillTexture, flowingTexture, (properties, st, ft) -> new ColoredFluidType(properties, st, ft, color), p -> VirtualFluidWithContainer.createSource(p, bucket), p -> VirtualFluidWithContainer.createFlowing(p, bucket));
    };

    public FluidBuilder<VirtualFluidWithContainer, R> sharedSingleTextureVirtualContainerFluid(SharedFeatureFlag featureFlag, String name, ItemLike bucket) {
        return sharedVirtualFluid(featureFlag, name, Petrolpark.asResource("fluid/"+name), Petrolpark.asResource("fluid/"+name), CreateRegistrate::defaultFluidType, p -> VirtualFluidWithContainer.createSource(p, bucket), p -> VirtualFluidWithContainer.createFlowing(p, bucket));
    };

    public <T extends BaseFlowingFluid> FluidBuilder<T, R> sharedVirtualFluid(SharedFeatureFlag featureFlag, String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
		return sharedEntry(featureFlag, name, c -> new VirtualFluidBuilder<>(self(), self(), name, c, stillTexture, flowingTexture, typeFactory, sourceFactory, flowingFactory)).asOptional();
	};
};
