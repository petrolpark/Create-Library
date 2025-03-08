package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.PetrolparkConfig;
import com.petrolpark.contamination.IContamination;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.recipe.advancedprocessing.firsttimelucky.FirstTimeLuckyRecipesBehaviour;
import com.petrolpark.recipe.advancedprocessing.firsttimelucky.IFirstTimeLuckyRecipe;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

@Mixin(value = MillstoneBlockEntity.class, remap = false)
public abstract class MillstoneBlockEntityMixin extends KineticBlockEntity {

    @Unique
    ItemStack lastItemProcessed;

    @Shadow
    private MillingRecipe lastRecipe;

    @Shadow
    public ItemStackHandler inputInv;

    @Shadow
    public ItemStackHandler outputInv;
    
    public MillstoneBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError(); // Should never be called
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/millstone/MillstoneBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.add(new FirstTimeLuckyRecipesBehaviour(this, r -> r.getType() == AllRecipeTypes.MILLING.getType()));
    };

    /**
     * Allow first-time lucky milling recipes to guarantee outputs the first time they are done by a player.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/millstone/MillstoneBlockEntity;process()V",
        at = @At("HEAD"),
        remap = false
    )
    public void inProcessStart(CallbackInfo ci) {
        lastItemProcessed = inputInv.getStackInSlot(0).copy();
    };

    /**
     * Allow first-time lucky milling recipes to guarantee outputs the first time they are done by a player.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/millstone/MillstoneBlockEntity;process()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/millstone/MillingRecipe;rollResults()Ljava/util/List;"
        ),
        cancellable = true,
        remap = false
    )
    @SuppressWarnings("unchecked")
    public void inProcessEnd(CallbackInfo ci) {
        FirstTimeLuckyRecipesBehaviour behaviour = getBehaviour(FirstTimeLuckyRecipesBehaviour.TYPE);
        List<ItemStack> results;

        if (behaviour != null && lastRecipe instanceof IFirstTimeLuckyRecipe ftlr) {
            results = ftlr.rollLuckyResults(behaviour.getPlayer());
        } else {
            results = lastRecipe.rollResults();
        };

        if (PetrolparkConfig.SERVER.createCrushingRecipesPropagateContaminants.get() && lastItemProcessed != null) {
            IContamination<?, ?> inputContamination = ItemContamination.get(lastItemProcessed);
            results.stream().map(ItemContamination::get).forEach(c -> c.contaminateAll(inputContamination.streamAllContaminants()));
        };

        results.forEach(stack -> {
            IDecayingItem.startDecay(stack);
            ItemHandlerHelper.insertItemStacked(outputInv, stack, false);
        });
        award(AllAdvancements.MILLSTONE);
        notifyUpdate();
        ci.cancel();
    };
};
