package com.petrolpark.shared.world.item;

import com.petrolpark.core.world.item.DrinkableFluidContainerItem;
import com.petrolpark.shared.ISharedFeature;
import com.petrolpark.shared.SharedFeatureFlag;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public class SharedDrinkableFluidContainerItem extends DrinkableFluidContainerItem implements ISharedFeature {

    public static final SharedDrinkableFluidContainerItem sharedDrinkableBottle(SharedFeatureFlag featureFlag, NonNullSupplier<Fluid> fluid, Item.Properties properties) {
        return new SharedDrinkableFluidContainerItem(featureFlag, fluid, 250, Items.GLASS_BOTTLE, properties);
    };

    protected final SharedFeatureFlag featureFlag;

    public SharedDrinkableFluidContainerItem(SharedFeatureFlag featureFlag, NonNullSupplier<Fluid> fluid, int containerVolume, ItemLike emptyContainer, Item.Properties properties) {
        super(fluid, containerVolume, emptyContainer, properties);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return featureFlag;
    };
    
};
