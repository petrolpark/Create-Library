package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.core.contamination.IContamination;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.IItemStackDuck;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.util.ItemHelper;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IItemStackDuck {

    @Unique
    private IContamination<?, ?> contamination;

    @ModifyReturnValue(
        method = "copy",
        at = @At("RETURN")
    )
    public ItemStack modifyCopy(ItemStack itemStack) {
        return ItemDecay.checkDecay(itemStack);
    };

    /**
     * Replace Items with their Decaying forms for comparison.
     * @param stack
     * @param otherStack
     * @param cir
     */
    @Inject(
        method = "isSameItemSameComponents",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void inIsSameItemSameComponents(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ItemHelper.equalIgnoringComponents(stack, otherStack));
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
