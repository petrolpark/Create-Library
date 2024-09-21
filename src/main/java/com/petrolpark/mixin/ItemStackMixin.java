package com.petrolpark.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.itemdecay.IDecayingItem;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    
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
    private static void inIsSameItemSameTags(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack trueStack = IDecayingItem.checkDecay(stack);
        ItemStack otherTrueStack = IDecayingItem.checkDecay(otherStack);
        if (!trueStack.is(otherTrueStack.getItem())) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(trueStack.isEmpty() && otherTrueStack.isEmpty() ? true : Objects.equals(trueStack.getTag(), otherTrueStack.getTag()) && trueStack.areCapsCompatible(otherTrueStack));
        }
    };
};
