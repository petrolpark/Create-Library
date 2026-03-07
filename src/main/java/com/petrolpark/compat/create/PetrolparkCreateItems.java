package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonItemTag;

import com.petrolpark.PetrolparkFoods;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.DrinkableFluidContainerItem;
import com.petrolpark.core.item.MilkCurativeBucketItem;
import com.petrolpark.core.world.fluid.FluidContainerItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.BucketItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;

public class PetrolparkCreateItems {

    public static final ItemEntry<BucketItem>
    
    BLOOD_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.BLOOD, "blood_bucket", p -> new BucketItem(PetrolparkCreateFluids.BLOOD.get(), p))
        .defaultModel()
        .tag(Tags.Items.BUCKETS, commonItemTag("buckets/blood"))
        .register();

    public static final ItemEntry<MilkCurativeBucketItem>
        
    CREAM_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "cream_bucket", p -> new MilkCurativeBucketItem(PetrolparkCreateFluids.CREAM.get(), p))
        .defaultModel()
        .properties(p -> p
            .food(PetrolparkFoods.CREAM)
        ).tag(Tags.Items.BUCKETS, commonItemTag("buckets/cream"))
        .register(),

    SKIMMED_MILK_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk_bucket", p -> new MilkCurativeBucketItem(PetrolparkCreateFluids.SKIMMED_MILK.get(), p))
        .defaultModel()
        .tag(Tags.Items.BUCKETS, Tags.Items.BUCKETS_MILK, commonItemTag("buckets/milk/skimmed"))
        .register();

    public static final ItemEntry<DrinkableFluidContainerItem> SUNFLOWER_OIL_BOTTLE = REGISTRATE.sharedItem(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil_bottle", p -> DrinkableFluidContainerItem.drinkableBottle(() -> PetrolparkCreateFluids.SUNFLOWER_OIL.get().getSource(), p))
        .capability(Capabilities.FluidHandler.ITEM, FluidContainerItem::provideCapability)
        .defaultModel()
        .properties(p -> p
            .food(PetrolparkFoods.OIL)
        ).tag(Tags.Items.DRINK_CONTAINING_BOTTLE, PetrolparkTags.Items.SLIPPING_POTION_INGREDIENTS.tag)
        .register();
    
    public static final ItemEntry<SequencedAssemblyItem> UNPROCESSED_MASHED_POTATO = REGISTRATE.item("unprocessed_mashed_potato", SequencedAssemblyItem::new)
        .defaultModel()
        .register();

    public static final void register() {};
};
