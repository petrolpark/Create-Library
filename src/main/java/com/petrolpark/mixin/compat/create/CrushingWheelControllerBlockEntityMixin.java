package com.petrolpark.mixin.compat.create;

import java.util.List;
import java.util.Optional;

import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.compat.create.core.recipe.firsttimelucky.FTLRecipesBehaviour;
import com.petrolpark.compat.create.core.recipe.firsttimelucky.IFTLProcessingRecipe;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.IDecayingItem;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public abstract class CrushingWheelControllerBlockEntityMixin extends SmartBlockEntity {

    @Unique
    private ItemStack lastItemProcessed = ItemStack.EMPTY;

    @Shadow
    public ProcessingInventory inventory;

    //TODO get the player in the controller block entity

    public CrushingWheelControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.add(new FTLRecipesBehaviour(this, rh -> rh.value() instanceof AbstractCrushingRecipe));
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At("HEAD"),
        remap = false
    )
    public void inApplyRecipeStart(CallbackInfo ci) {
        lastItemProcessed = inventory.getStackInSlot(0).copy();
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    @SuppressWarnings("unchecked")
    public void inApplyRecipeMiddle(CallbackInfo ci, Optional<RecipeHolder<ProcessingRecipe<RecipeWrapper>>> recipe, List<ItemStack> rolledResults, int rolls, int slot) {
        if (slot == 0) {
            FTLRecipesBehaviour behaviour = getBehaviour(FTLRecipesBehaviour.TYPE);
            if (behaviour != null && recipe.get().value() instanceof IFTLProcessingRecipe ftlr) {
                List<ItemStack> results = ftlr.rollLuckyResults(behaviour.getPlayer());
                rolledResults.clear();
                rolledResults.addAll(results);
            };
        };
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inApplyRecipeEnd(CallbackInfo ci, Optional<RecipeHolder<ProcessingRecipe<RecipeWrapper>>> recipe, List<ItemStack> list) {
        list.forEach(IDecayingItem::startDecay);
        if (PetrolparkConfig.SERVER.createCrushingRecipesPropagateContaminants.get() && lastItemProcessed != null) {
            IContamination<?, ?> inputContamination = ItemContamination.get(lastItemProcessed);
            Level level = getLevel();
            if (level != null) list.stream().map(ItemContamination::get).forEach(c -> c.contaminateAll(level.registryAccess(), inputContamination.streamAllContaminants()));
        };
    };
    
    
};
