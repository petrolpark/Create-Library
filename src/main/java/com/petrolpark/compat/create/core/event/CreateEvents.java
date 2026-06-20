package com.petrolpark.compat.create.core.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.compat.create.core.world.block.CopycatBlockConversion;
import com.petrolpark.compat.create.core.world.block.chainConveyor.ChainConveyorItemEvent;
import com.petrolpark.compat.create.core.world.block.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.shared.content.kinetics.horseMill.HorseMillContraptionEntity;
import com.petrolpark.compat.create.shared.content.processing.blender.BlenderBlockEntity;
import com.petrolpark.compat.create.shared.content.processing.centrifuge.CentrifugationEvent;
import com.petrolpark.compat.create.shared.content.processing.centrifuge.PotionCentrifugation;
import com.petrolpark.core.world.entity.EntityFallOnEvent;
import com.petrolpark.core.world.item.decay.IApplyDecayRecipe;
import com.petrolpark.shared.SharedFeatureFlag;
import com.petrolpark.shared.registry.SharedRecipeTypes;
import com.petrolpark.shared.world.GoldConversion;
import com.petrolpark.shared.world.GoldConversion.RegisterGoldBlockStateConversionEvent;
import com.petrolpark.shared.world.GoldConversion.RegisterGoldItemConversionEvent;
import com.petrolpark.util.Conversion;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

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
