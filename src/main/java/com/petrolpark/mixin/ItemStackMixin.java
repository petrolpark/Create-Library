package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.petrolpark.contamination.IContamination;
import com.petrolpark.contamination.IItemStackDuck;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.IDecayingItem;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IItemStackDuck {

    @Unique
    private IContamination<?, ?> contamination;
    
    // TODO replace with ModifyReturnValue from mixin extras
    @Inject(
        method = "copy",
        at = @At("RETURN"),
        cancellable = true
    )
    public void inCopy(CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(IDecayingItem.checkDecay(cir.getReturnValue()));
    };

    @Inject(
        method = "isSameItemSameTags",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void inIsSameItemSameTags(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly=true, ordinal=0) LocalRef<ItemStack> arg1, @Local(argsOnly=true, ordinal=1) LocalRef<ItemStack> arg2) {
        arg1.set(IDecayingItem.checkDecay(stack));
        arg2.set(IDecayingItem.checkDecay(otherStack));
    }

    @Override
    public IContamination<?, ?> getContamination() {
        if (contamination == null) contamination = ItemContamination.create(self());
        return contamination;
    };

    @Override
    public void onContaminationSaved() {
        contamination = null;
    };

    private ItemStack self() {
        return (ItemStack)(Object)this;  
    };
};
