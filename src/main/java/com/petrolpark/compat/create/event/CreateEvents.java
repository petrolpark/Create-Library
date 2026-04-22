package com.petrolpark.compat.create.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.kinetics.horseMill.HorseMillContraptionEntity;
import com.petrolpark.compat.create.common.processing.blender.BlenderBlockEntity;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugationEvent;
import com.petrolpark.compat.create.common.processing.centrifuge.PotionCentrifugation;
import com.petrolpark.compat.create.common.processing.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.core.block.CopycatBlockConversion;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorItemEvent;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.world.entity.EntityFallOnEvent;
import com.petrolpark.util.Conversion;
import com.petrolpark.util.GoldHelper;
import com.petrolpark.util.GoldHelper.RegisterGoldBlockStateConversionEvent;
import com.petrolpark.util.GoldHelper.RegisterGoldItemConversionEvent;
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
        event.register(Petrolpark.asResource("create/copycats"), new CopycatBlockConversion(GoldHelper::convertBlockStateToGold), 5000);
    };
    
    @SubscribeEvent
    public static final void onChainConveyorAddItem(ChainConveyorItemEvent.Add event) {
        final ItemStack stack = IApplyDecayRecipe.withAppliedDecay(event.level, PetrolparkRecipeTypes.DRYING.get(), event.getStack(), !event.simulate);
        if (!ItemStack.isSameItemSameComponents(stack, event.getStack())) event.setTransformedStack(stack);
    };

    @SubscribeEvent
    public static final void onChainConveyorRemoveItem(ChainConveyorItemEvent.Remove event) {
        event.setTransformedStack(IApplyDecayRecipe.withAppliedDecayRemoved(event.level, PetrolparkRecipeTypes.DRYING.get(), event.getStack()));
    };

    @SubscribeEvent
    public static final void onChainConveyorAddItemClient(ChainConveyorItemEvent.AddClient event) {
        event.or(!event.level.getRecipeManager().getRecipesFor(PetrolparkRecipeTypes.DRYING.get(), new SingleRecipeInput(event.stack), event.level).isEmpty());
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
