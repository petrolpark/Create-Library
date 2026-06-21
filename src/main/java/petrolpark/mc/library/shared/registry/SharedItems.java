package petrolpark.mc.library.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;
import static petrolpark.mc.library.PetrolparkTags.commonItemTag;

import petrolpark.mc.library.registry.PetrolparkExperimentalFeatureFlags;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.SharedItem;
import petrolpark.mc.library.shared.world.item.shulkerbelt.ShulkerBeltItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.Tags;

public class SharedItems {

    public static final ItemEntry<ShulkerBeltItem> SHULKER_BELT = REGISTRATE.item("shulker_belt", ShulkerBeltItem::new)
        .properties(p -> p
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .requiredFeatures(PetrolparkExperimentalFeatureFlags.EXTENDED_INVENTORY.featureFlag)
        ).register();
    
    public static final ItemEntry<SharedItem>

    BUTTER = REGISTRATE.sharedItem(SharedFeatureFlag.MILK_PRODUCTS, "butter", SharedItem::new)
        .properties(p -> p
            .food(SharedFoods.BUTTER)
        ).defaultModel()
        .tag(Tags.Items.FOODS, commonItemTag("foods/butter"))
        .register(),
    FRIES = REGISTRATE.sharedItem(SharedFeatureFlag.FRIES, "fries", SharedItem::new)
        .properties(p -> p
            .food(SharedFoods.FRIES)
        ).defaultModel()
        .tag(Tags.Items.FOODS, commonItemTag("foods/fries"))
        .register(),
    MASHED_POTATO = REGISTRATE.sharedItem(SharedFeatureFlag.POTATO_PRODUCTS, "mashed_potato", SharedItem::new)
        .properties(p -> p
            .food(SharedFoods.MASHED_POTATO)
        ).tag(Tags.Items.FOODS, commonItemTag("foods/mashed_potato"))
        .defaultModel()
        .register(),
    MESH = REGISTRATE.sharedItem(SharedFeatureFlag.MESH, "mesh", SharedItem::new)
        .defaultModel()
        .register(),
    RAW_FRIES = REGISTRATE.sharedItem(SharedFeatureFlag.FRIES, "raw_fries", SharedItem::new)
        .properties(p -> p
            .food(SharedFoods.RAW_FRIES)
        ).defaultModel()
        .tag(Tags.Items.FOODS)
        .register(),
    EGGSHELL = REGISTRATE.sharedItem(SharedFeatureFlag.EGG_PRODUCTS, "eggshell", SharedItem::new)
        .defaultModel()
        .tag(commonItemTag("eggshells"))
        .register(),
    YOLK = REGISTRATE.sharedItem(SharedFeatureFlag.EGG_PRODUCTS, "yolk", SharedItem::new)
        .defaultModel()
        .properties(p -> p
            .food(SharedFoods.YOLK)
        ).register();

    public static final void register() {};
};
