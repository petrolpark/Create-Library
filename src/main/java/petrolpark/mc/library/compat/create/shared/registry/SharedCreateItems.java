package petrolpark.mc.library.compat.create.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;
import static petrolpark.mc.library.PetrolparkTags.commonItemTag;

import java.util.function.Supplier;

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
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistrateProviderTypes;
import petrolpark.mc.library.core.registrate.AbstractPetrolparkRegistrate;
import petrolpark.mc.library.core.world.item.FluidContainerItem;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedFoods;
import petrolpark.mc.library.shared.world.item.SharedBucketItem;
import petrolpark.mc.library.shared.world.item.SharedDrinkableFluidContainerItem;
import petrolpark.mc.library.shared.world.item.SharedMilkCurativeBucketItem;

public class SharedCreateItems {

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
    
    public static final ItemEntry<SequencedAssemblyItem> UNPROCESSED_MASHED_POTATO = REGISTRATE.sharedItem(SharedFeatureFlag.POTATO_PRODUCTS, "unprocessed_mashed_potato", SequencedAssemblyItem::new)
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

    //TODO deal with Flags in fluid handler
    public static final <R extends AbstractPetrolparkRegistrate<R>> ItemBuilder<SharedBucketItem, R> sharedBucketItem(R registrate, SharedFeatureFlag featureFlag, String name, Supplier<FluidEntry<? extends Fluid>> fluid) {
        return registrate.sharedItem(featureFlag, name + "_bucket", (p, f) -> new SharedBucketItem(f, fluid.get().get(), p))
            .fluidCapability((stack, v) -> new FluidBucketWrapper(stack))
            .defaultModel()
            .tag(Tags.Items.BUCKETS, commonItemTag("buckets/" + name));
    };
};
