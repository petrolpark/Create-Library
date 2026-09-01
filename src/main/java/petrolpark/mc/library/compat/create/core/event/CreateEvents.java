package petrolpark.mc.library.compat.create.core.event;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.world.block.CopycatBlockConversion;
import petrolpark.mc.library.compat.create.core.world.block.chainConveyor.ChainConveyorItemEvent;
import petrolpark.mc.library.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlock;
import petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.blender.BlenderBlockEntity;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.CentrifugationEvent;
import petrolpark.mc.library.compat.create.shared.content.processing.centrifuge.PotionCentrifugation;
import petrolpark.mc.library.core.world.entity.EntityFallOnEvent;
import petrolpark.mc.library.core.world.item.decay.IApplyDecayRecipe;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;
import petrolpark.mc.library.shared.world.GoldConversion;
import petrolpark.mc.library.shared.world.GoldConversion.RegisterGoldBlockStateConversionEvent;
import petrolpark.mc.library.shared.world.GoldConversion.RegisterGoldItemConversionEvent;
import petrolpark.mc.library.util.Conversion;

public class CreateEvents {

    @SubscribeEvent
    public static final void onRegisterGoldItemConversions(RegisterGoldItemConversionEvent event) {
        event.register(Petrolpark.asResource("create/plates"), Conversion.convertTaggedItemStrict(AllItemTags.PLATES.tag, AllItems.GOLDEN_SHEET), 1000);
        event.register(Petrolpark.asResource("create/crushed_ores"), Conversion.convertTaggedItemStrict(AllItemTags.CRUSHED_RAW_MATERIALS.tag, AllItems.CRUSHED_GOLD), 1000);
    };

    @SubscribeEvent
    public static final void onRegisterGoldBlockStateConversions(RegisterGoldBlockStateConversionEvent event) {
        event.register(Petrolpark.asResource("create/copycats"), new CopycatBlockConversion(GoldConversion::convertBlockStateToGold), 5000);
    };
    
    @SubscribeEvent
    public static final void onChainConveyorAddItem(ChainConveyorItemEvent.Add event) {
        final ItemStack stack = IApplyDecayRecipe.withAppliedDecay(event.level, SharedRecipeTypes.DRYING.get(), event.getStack(), !event.simulate);
        if (!ItemStack.isSameItemSameComponents(stack, event.getStack())) event.setTransformedStack(stack);
    };

    @SubscribeEvent
    public static final void onChainConveyorRemoveItem(ChainConveyorItemEvent.Remove event) {
        event.setTransformedStack(IApplyDecayRecipe.withAppliedDecayRemoved(event.level, SharedRecipeTypes.DRYING.get(), event.getStack()));
    };

    @SubscribeEvent
    public static final void onChainConveyorAddItemClient(ChainConveyorItemEvent.AddClient event) {
        event.or(!event.level.getRecipeManager().getRecipesFor(SharedRecipeTypes.DRYING.get(), new SingleRecipeInput(event.stack), event.level).isEmpty());
    };

    @SubscribeEvent
    public static final void onCentrifugation(CentrifugationEvent event) {
        PotionCentrifugation.onCentrifugation(event);
    };

    @SubscribeEvent
    public static final void onEntityFallOn(EntityFallOnEvent event) {
        if (SharedFeatureFlag.BLENDER.enabled()) BlenderBlockEntity.onEntityFallOn(event);
    };

    @SubscribeEvent
    public static final void onUseItem(UseItemOnBlockEvent event) {
        EncasedCrushingWheelControllerBlock.onItemUsed(event);
    };

    @SubscribeEvent
    public static final void onEntityTickPost(EntityTickEvent.Post event) {
        HorseMillContraptionEntity.onEntityTickPost(event);
    };
};
