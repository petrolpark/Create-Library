package petrolpark.mc.library.shared;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.core.world.block.chainConveyor.ChainConveyorArmInteractionPoint;
import petrolpark.mc.library.compat.create.core.world.dough.DoughBlock;
import petrolpark.mc.library.compat.create.core.world.dough.IDough;
import petrolpark.mc.library.compat.create.core.world.dough.cookieCutter.CookieCutterItem;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.RollingPinItem;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HarnessBlock;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillBearingBlock;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillProperties;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.BasinLidBlock;
import petrolpark.mc.library.compat.create.shared.content.processing.basinLid.LiddedBasinRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderBlock;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlendingRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugationRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugeBlock;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionDieBlock;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.BoilingRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.FilteringRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.JuicingRecipe;
import petrolpark.mc.library.compat.create.shared.content.processing.meshBasin.MeshBasinBlock;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlock;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateDataMapTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateFluids;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateItems;
import petrolpark.mc.library.core.event.CommonEvents;
import petrolpark.mc.library.registry.PetrolparkAttributes;
import petrolpark.mc.library.shared.registry.SharedBlocks;
import petrolpark.mc.library.shared.registry.SharedItems;
import petrolpark.mc.library.shared.registry.SharedLootTables;
import petrolpark.mc.library.shared.registry.SharedMobEffects;
import petrolpark.mc.library.shared.world.GoldConversion;
import petrolpark.mc.library.shared.world.item.crafting.drying.DryingRecipe;
import petrolpark.mc.library.shared.world.item.crafting.drying.rack.DryingRackBlock;
import petrolpark.mc.library.util.Lang;

/**
 * Shared Feature Flags are used for conditional registration, allowing mods to share features.
 * But the objects they register will not be registered if unnecessary, so as to not create confusion.
 * @see GetPetrolparkSharedFeatures Registering Shared Features
 */
public enum SharedFeatureFlag implements StringRepresentable {

    NONE,
    
    // INDEPDENDENT

    /**
     * Registers nothing, but enables the interaction of Mechanical Arms with Chain Conveyors.
     * Requires Create.
     * @see ChainConveyorArmInteractionPoint
     */
    ARMS_TARGET_CHAIN_CONVEYORS,
    /**
     * Registers Basin Lid block and default recipe, and {@code lidded_basin} recipe type.
     * Requires Create.
     * @see BasinLidBlock
     * @see LiddedBasinRecipe
     */
    BASIN_LID,
    /**
     * Registers Blender block and default recipe, and {@code blending} recipe type.
     * Requires Create.
     * @see BlenderBlock
     * @see BlendingRecipe
     */
    BLENDER,
    /**
     * Registers the {@link SharedCreateFluids#BLOOD Blood fluid}, and blending mobs if {@link SharedFeatureFlag#BLENDER} is enabled.
     * Requires Create.
     * @see BlenderBlockEntity#addHurtingEntity(net.minecraft.world.entity.LivingEntity) Blending mobs for blood
     * @see PetrolparkTags.EntityTypes#DOESNT_BLEED Excluding mobs from blending for Blood
     */
    BLOOD,
    /**
     * Registers the Centriuge block and default recipe, and {@code centrifugation} recipe type.
     * Requires Create.
     * @see CentrifugeBlock
     * @see CentrifugationRecipe
     */
    CENTRIFUGE,
    /**
     * Registers the Circular Cookie Cutter item
     * @see CookieCutterItem
     */
    CIRCLE_COOKIE_CUTTER,
    /**
     * Registers the Dough block and some Ponders
     * @see DoughBlock
     */
    DOUGH,
    /**
     * Registers the Drying Rack block and default recipe.
     * @see DryingRackBlock
     * @see DryingRecipe
     */
    DRYING_RACK,
    /**
     * Registers {@link SharedItems#EGGSHELL Eggshell} and {@link SharedItems#YOLK Yolk item}s, the {@link SharedCreateFluids#EGG_WHITE Egg White fluid} (if Create is present), and a {@link JuicingRecipe} recipe to obtain them if {@link SharedFeatureFlag#MESH_BASIN} is enabled.
     * Also enables a chance of creating an Eggshell when a thrown egg lands.
     * @see CommonEvents#onProjectileImpact Eggshells landing
     * @see SharedLootTables#EGG
     */
    EGG_PRODUCTS,
    /**
     * Registers all the default {@link GoldConversion}s and the {@link SharedMobEffects#MIDAS_TOUCH Midas' Touch effect}.
     * The {@link GoldConversion} API can still be used safely, but nothing will have any registered transformations. 
     */
    GOLD_CONVERSION,
    /**
     * Registers the Horse Mill Bearing and Harness blocks and default recipes.
     * The {@link HorseMillProperties} {@link SharedCreateDataMapTypes#HORSE_MILL_PROPERTIES} is always registered, even if this flag is not enabled. 
     * Requires Create.
     * @see HorseMillBearingBlock
     * @see HarnessBlock
     */
    HORSE_MILL,
    /**
     * Registers the {@link SharedItems#MESH Mesh item}, which is just used as a crafting ingredient for {@link SharedFeatureFlag#MESH_BASIN Mesh Basin} and {@link SharedFeatureFlag#EXTRUSION Extrusion Die}.
     */
    MESH,
    /**
     * Indev.
     */
    PROGRAMMING_BLOCK,
    /**
     * Registers the Redstone Programmer block, but <em>not</em> any default recipe.
     * Requires Create.
     * @see RedstoneProgrammerBlock
     */
    REDSTONE_PROGRAMMER,
    /**
     * Registers the {@link SharedMobEffects#SLIPPING Slipping effect and potions}.
     * The {@link PetrolparkAttributes#SLIPPERINESS corresponding attribute} is always registered, even if this flag is not enabled.
     */
    SLIPPING,

    @Deprecated SPRING,

    // FIRST-ORDER DEPENDENT
    
    /**
     * Registers the Extrusion Die block and default recipe, and {@code extrusion} recipe type.
     * Requires Create.
     * @see ExtrusionDieBlock
     * @see ExtrusionRecipe
     */
    EXTRUSION(MESH),
    /**
     * Indev.
     */
    MANDREL(SPRING),
    /**
     * Registers the Mesh Basin block and default recipe, and {@code juicing}, {@code boiling} and {@code filtering} recipe types.
     * Requires Create.
     * @see MeshBasinBlock
     * @see JuicingRecipe
     * @see BoilingRecipe
     * @see FilteringRecipe
     */
    MESH_BASIN(MESH),
    /**
     * Register the {@link SharedItems#BUTTER Butter item}, and {@link SharedCreateFluids#SKIMMED_MILK Skimmed Milk} and {@link SharedCreateFluids#CREAM Cream fluids} (if Create is present),
     * and the default recipes to obtain them (by {@link SharedFeatureFlag#CENTRIFUGE centrifugation}, again if Create is present).
     */
    MILK_PRODUCTS(CENTRIFUGE),
    /**
     * Register the {@link SharedItems#MASHED_POTATO Mashed Potato item} and {@link SharedBlocks#MASHED_POTATO_BLOCK block}, and the recipes for conversion between the two.
     * If Create is present, also registers the {@link SharedCreateItems#UNPROCESSED_MASHED_POTATO sequence assembly recipe} for Mashed Potato.
     */
    POTATO_PRODUCTS(MILK_PRODUCTS),
        /**
     * Registers the Rolling Pin item and its default recipes.
     * Technically doesn't require Create, but as {@link IDough} does, it's rather useless without.
     * @see RollingPinItem
     */
    ROLLING_PIN(DOUGH),
    /**
     * Register the {@link SharedCreateFluids#SUNFLOWER_OIL Sunflower Oil fluid} and the default recipe to obtain it.
     * Requires Create.
     */
    SUNFLOWER_OIL(SLIPPING),

    // SECOND-ORDER DEPENDENT

    /**
     * Registers the {@link SharedItems#FRIES Fries item}, {@link SharedBlocks#RAW_FRIES_BLOCK block} and {@link SharedItems#RAW_FRIES Raw Fries item}, and the Smelting/Smoking/Campfire Cooking recipe to cook them
     * If Create is present, enables the {@link SharedFeatureFlag#EXTRUSION extrusion} recipe to obtain the Raw Fries Block.
     * If {@link SharedFeatureFlag#MESH_BASIN} is enabled, disables the cooking recipes and enables the default deep-frying recipe. 
     */
    FRIES(POTATO_PRODUCTS, EXTRUSION)
    ;

    public static final Codec<SharedFeatureFlag> CODEC = StringRepresentable.fromEnum(SharedFeatureFlag::values);

    private final SharedFeatureFlag[] dependencies;
    
    private final SortedSet<Mods> users = new TreeSet<>(Mods::compareTo);
    private boolean enabled = false;

    SharedFeatureFlag(SharedFeatureFlag... dependencies) {
        this.dependencies = dependencies;
    };

    public boolean enabled() {
        return enabled;
    };

    public void enable(Mods mod) {
        enabled = true;
        users.add(mod);
        for (SharedFeatureFlag feature : dependencies) feature.enable(mod);
    };

    @ApiStatus.Internal
    public static void enableAll() {
        for (SharedFeatureFlag feature : values()) feature.enable(Mods.PETROLPARK);
    };

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    };

    public Stream<Mods> streamUsers() {
        return users.stream();
    };
};
