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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.compat.create.core.data.recipe.firstTimeLucky.FTLRecipesBehaviour;
import com.petrolpark.compat.create.core.data.recipe.firstTimeLucky.IFTLProcessingRecipe;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.flags.IFlagPole;
import com.petrolpark.core.flags.ItemFlagPole;
import com.petrolpark.core.world.item.decay.ItemDecay;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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

    public CrushingWheelControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void petrolpark$addFirstTimeLuckyRecipeBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.add(new FTLRecipesBehaviour(this, rh -> rh.value() instanceof AbstractCrushingRecipe));
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At("HEAD"),
        remap = false
    )
    public void petrolpark$storeProcessedItem(CallbackInfo ci) {
        lastItemProcessed = inventory.getStackInSlot(0).copy();
    };

    @WrapOperation(
        method = "applyRecipe()V",
        at = @At(
            value = "INVOKE",
            target = "rollResults"
        )
    )
    @SuppressWarnings("unchecked")
    public List<ItemStack> petrolpark$getLuckyResults(StandardProcessingRecipe<RecipeWrapper> recipe, RandomSource random, Operation<List<ItemStack>> original) {
        if (recipe instanceof IFTLProcessingRecipe ftlr) return ftlr.rollLuckyResults(this, random);
        else return original.call(recipe, random);
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void petrolpark$propagateFlags(CallbackInfo ci, Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> recipe, List<ItemStack> list) {
        list.forEach(ItemDecay::startDecay);
        if (PetrolparkConfigs.server().createCrushingRecipesPropagateFlags.get() && lastItemProcessed != null) {
            IFlagPole<?, ?> inputFlags = ItemFlagPole.get(lastItemProcessed);
            Level level = getLevel();
            if (level != null) list.stream().map(ItemFlagPole::get).forEach(c -> c.flagAll(inputFlags.streamAllFlags()));
        };
    };
    
    
};
