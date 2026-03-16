package com.petrolpark.compat.create;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonItemTag;

import com.petrolpark.PetrolparkFoods;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.item.SharedBucketItem;
import com.petrolpark.core.item.SharedDrinkableFluidContainerItem;
import com.petrolpark.core.item.SharedMilkCurativeBucketItem;
import com.petrolpark.core.world.fluid.FluidContainerItem;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class PetrolparkCreateItems {

    public static final ItemEntry<SharedBucketItem>
    
    BLOOD_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.BLOOD, "blood_bucket", (p, f) -> new SharedBucketItem(f, PetrolparkCreateFluids.BLOOD.get(), p))
        .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
        .defaultModel()
        .tag(Tags.Items.BUCKETS, commonItemTag("buckets/blood"))
        .register();

    public static final ItemEntry<SharedMilkCurativeBucketItem>
        
    CREAM_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "cream_bucket", (p, f) -> new SharedMilkCurativeBucketItem(f, PetrolparkCreateFluids.CREAM.get(), p))
        .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
        .defaultModel()
        .properties(p -> p
            .food(PetrolparkFoods.CREAM)
        ).tag(Tags.Items.BUCKETS, commonItemTag("buckets/cream"))
        .register(),

    SKIMMED_MILK_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk_bucket", (p, f) -> new SharedMilkCurativeBucketItem(f, PetrolparkCreateFluids.SKIMMED_MILK.get(), p))
        .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
        .defaultModel()
        .tag(Tags.Items.BUCKETS, Tags.Items.BUCKETS_MILK, commonItemTag("buckets/milk/skimmed"))
        .register();

    public static final ItemEntry<SharedDrinkableFluidContainerItem> SUNFLOWER_OIL_BOTTLE = REGISTRATE.sharedItem(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil_bottle", (p, f) -> SharedDrinkableFluidContainerItem.sharedDrinkableBottle(f, () -> PetrolparkCreateFluids.SUNFLOWER_OIL.get().getSource(), p))
        .fluidCapability(FluidContainerItem::provideCapability)
        .defaultModel()
        .properties(p -> p
            .food(PetrolparkFoods.OIL)
        ).tag(Tags.Items.DRINK_CONTAINING_BOTTLE, PetrolparkTags.Items.SLIPPING_POTION_INGREDIENTS.tag)
        .register();
    
    public static final ItemEntry<SequencedAssemblyItem> UNPROCESSED_MASHED_POTATO = REGISTRATE.item("unprocessed_mashed_potato", SequencedAssemblyItem::new)
        .defaultModel()
        .register();

    public static final void register() {};

    public static final <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> potatoCannonProjectile(NonNullBiFunction<DataGenContext<Item, T>, PotatoCannonProjectileType.Builder, PotatoCannonProjectileType.Builder> builderTransformer) {
        return potatoCannonProjectile(null, builderTransformer);
    };

    public static final <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> potatoCannonProjectile(String name, NonNullBiFunction<DataGenContext<Item, T>, PotatoCannonProjectileType.Builder, PotatoCannonProjectileType.Builder> builderTransformer) {
        return b -> b.setData(PetrolparkCreateRegistrateProviderTypes.POTATO_CANNON_PROJECTILE, (ctx, prov) -> prov.register(
            name == null ? ctx.getId() : ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), name),
            builderTransformer.apply(ctx, new PotatoCannonProjectileType.Builder().addItems(ctx.getEntry())).build()
        ));
    };
};
