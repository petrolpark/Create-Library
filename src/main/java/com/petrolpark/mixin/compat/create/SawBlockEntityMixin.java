package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.IDecayingItem;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class SawBlockEntityMixin extends BlockBreakingKineticBlockEntity {

    public SawBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Shadow
    public ProcessingInventory inventory;

    @Unique
    ItemStack petrolpark$lastItemProcessed;

    @Inject(
        method = "applyRecipe()V",
        at = @At("HEAD"),
        remap = false
    )
    public void inApplyRecipeStart(CallbackInfo ci) {
        petrolpark$lastItemProcessed = inventory.getStackInSlot(0);
    };

    @Inject(
        method = "applyRecipe()V",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    public void inApplyRecipeEnd(CallbackInfo ci, ItemStack input, List<? extends Recipe<?>> recipes) {
        if (recipes.isEmpty()) return;
        IContamination<?, ?> inputContamination = ItemContamination.get(petrolpark$lastItemProcessed);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            IDecayingItem.startDecay(stack);
            Level level = getLevel();
            if (level != null && PetrolparkConfig.SERVER.createCuttingRecipesPropagateContaminants.get()) ItemContamination.get(stack).contaminateAll(level.registryAccess(), inputContamination.streamAllContaminants());
        };
    };
};
