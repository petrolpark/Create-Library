package com.petrolpark.compat.create.shared.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;
import static com.petrolpark.PetrolparkTags.commonItemTag;

import java.util.function.Supplier;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.core.world.dough.rollingPin.RollingPinItem;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistrateProviderTypes;
import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.petrolpark.core.world.item.FluidContainerItem;
import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.shared.registry.SharedFoods;
import com.petrolpark.shared.world.item.SharedBucketItem;
import com.petrolpark.shared.world.item.SharedDrinkableFluidContainerItem;
import com.petrolpark.shared.world.item.SharedMilkCurativeBucketItem;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class SharedCreateItems {

    public static final ItemEntry<RollingPinItem> ROLLING_PIN = REGISTRATE.sharedItem(SharedFeatureFlag.ROLLING_PIN, "rolling_pin", RollingPinItem::new)    
        .properties(p -> p
            .stacksTo(1)
        ).register();

    public static final ItemEntry<SharedBucketItem>
    
    BLOOD_BUCKET = sharedBucketItem(REGISTRATE, SharedFeatureFlag.BLOOD, "blood", () -> SharedCreateFluids.BLOOD)
        .register();

    public static final ItemEntry<SharedMilkCurativeBucketItem>
        
    CREAM_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "cream_bucket", (p, f) -> new SharedMilkCurativeBucketItem(f, SharedCreateFluids.CREAM.get(), p))
        .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
        .defaultModel()
        .properties(p -> p
            .food(SharedFoods.CREAM)
        ).tag(Tags.Items.BUCKETS, commonItemTag("buckets/cream"))
        .register(),

    SKIMMED_MILK_BUCKET = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "skimmed_milk_bucket", (p, f) -> new SharedMilkCurativeBucketItem(f, SharedCreateFluids.SKIMMED_MILK.get(), p))
        .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
        .defaultModel()
        .tag(Tags.Items.BUCKETS, Tags.Items.BUCKETS_MILK, commonItemTag("buckets/milk/skimmed"))
        .register();

    public static final ItemEntry<SharedDrinkableFluidContainerItem> SUNFLOWER_OIL_BOTTLE = REGISTRATE.sharedItem(SharedFeatureFlag.SUNFLOWER_OIL, "sunflower_oil_bottle", (p, f) -> SharedDrinkableFluidContainerItem.sharedDrinkableBottle(f, () -> SharedCreateFluids.SUNFLOWER_OIL.get().getSource(), p))
        .fluidCapability(FluidContainerItem::provideCapability)
        .defaultModel()
        .properties(p -> p
            .food(SharedFoods.OIL)
        ).tag(Tags.Items.DRINK_CONTAINING_BOTTLE, PetrolparkTags.Items.SLIPPING_POTION_INGREDIENTS.tag)
        .register();

    public static final ItemEntry<SharedDrinkableFluidContainerItem> EGG_WHITE_BOTTLE = REGISTRATE.sharedItem(SharedFeatureFlag.EGG_PRODUCTS, "egg_white_bottle", (p, f) -> SharedDrinkableFluidContainerItem.sharedDrinkableBottle(f, () -> SharedCreateFluids.EGG_WHITE.get().getSource(), p))
        .fluidCapability(FluidContainerItem::provideCapability)
        .defaultModel()
        .properties(p -> p
            .food(SharedFoods.EGG_WHITE)
        ).tag(Tags.Items.DRINK_CONTAINING_BOTTLE)
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

    public static final <R extends AbstractPetrolparkRegistrate<R>> ItemBuilder<SharedBucketItem, R> sharedBucketItem(R registrate, SharedFeatureFlag featureFlag, String name, Supplier<FluidEntry<? extends Fluid>> fluid) {
        return registrate.sharedItem(featureFlag, name + "_bucket", (p, f) -> new SharedBucketItem(f, fluid.get().get(), p))
            .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
            .defaultModel()
            .tag(Tags.Items.BUCKETS, commonItemTag("buckets/" + name));
    };
};
