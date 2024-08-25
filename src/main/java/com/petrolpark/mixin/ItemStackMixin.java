package com.petrolpark.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
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

    @Overwrite
    public static boolean isSameItemSameTags(ItemStack stack, ItemStack otherStack) {
        ItemStack trueStack = IDecayingItem.checkDecay(stack);
        ItemStack otherTrueStack = IDecayingItem.checkDecay(otherStack);
        if (!trueStack.is(otherTrueStack.getItem())) {
            return false;
        } else {
            return trueStack.isEmpty() && otherTrueStack.isEmpty() ? true : Objects.equals(trueStack.getTag(), otherTrueStack.getTag()) && trueStack.areCapsCompatible(otherTrueStack);
        }
    };
};
