package com.petrolpark.compat.create.core;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.AbstractPetrolparkRegistrate;
import com.petrolpark.Petrolpark;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.core.dough.DoughData;
import com.petrolpark.compat.create.core.dough.ingredient.DoughIngredient;
import com.petrolpark.core.fluid.ColoredFluidType;
import com.petrolpark.core.recipe.ingredient.advanced.GenericAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.ITypelessAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.NamedAdvancedIngredientType;
import com.petrolpark.core.registrate.builder.SharedCreateBlockEntityBuilder;
import com.petrolpark.core.world.fluid.VirtualFluidWithContainer;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.VirtualFluidBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

    public RegistryEntry<IAdvancedIngredientType<? super DoughData>, NamedAdvancedIngredientType<DoughData>> doughIngredientType(String name, MapCodec<? extends DoughIngredient> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends DoughIngredient> streamCodec) {
        return simple(name, PetrolparkCreateRegistries.Keys.DOUGH_INGREDIENT_TYPE, () -> new NamedAdvancedIngredientType<>(Util.makeDescriptionId("advancedIngredient.dough", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IAdvancedIngredientType<? super DoughData>, IAdvancedIngredientType<? super DoughData>> doughIngredientType(String name, IAdvancedIngredientType<? super DoughData> type) {
        return simple(name, PetrolparkCreateRegistries.Keys.DOUGH_INGREDIENT_TYPE, () -> type);
    };


    public RegistryEntry<IAdvancedIngredientType<? super DoughData>, INamedAdvancedIngredientType<DoughData>> doughIngredientType(String name, NonNullFunction<String, INamedAdvancedIngredientType<DoughData>> typeFactory) {
        return simple(name, PetrolparkCreateRegistries.Keys.DOUGH_INGREDIENT_TYPE, () -> typeFactory.apply(Util.makeDescriptionId("advancedIngredient", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };

    public <TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<DoughData>> RegistryEntry<IAdvancedIngredientType<? super DoughData>, GenericAdvancedIngredientType<DoughData, TYPELESS_INGREDIENT>> doughIngredientType(String name, Function<Codec<IAdvancedIngredient<? super DoughData>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super DoughData>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory) {
        return genericAdvancedIngredientType(PetrolparkCreateRegistries.Keys.DOUGH_INGREDIENT_TYPE, DoughIngredient.CODEC, DoughIngredient.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };
};
