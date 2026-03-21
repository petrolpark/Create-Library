package com.petrolpark.compat.create.event;

import com.petrolpark.PetrolparkRecipeTypes;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.common.processing.blender.BlenderBlockEntity;
import com.petrolpark.compat.create.common.processing.centrifuge.CentrifugationEvent;
import com.petrolpark.compat.create.common.processing.centrifuge.PotionCentrifugation;
import com.petrolpark.compat.create.common.processing.crushingWheel.EncasedCrushingWheelControllerBlock;
import com.petrolpark.compat.create.core.chainconveyor.ChainConveyorItemEvent;
import com.petrolpark.core.item.decay.IApplyDecayRecipe;
import com.petrolpark.core.world.entity.EntityFallOnEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class CreateEvents {
    
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
};
