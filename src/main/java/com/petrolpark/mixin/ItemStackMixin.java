package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
    public ItemStack petrolpark$startDecay(ItemStack itemStack) {
        return ItemDecay.checkDecay(itemStack);
    };

    /**
     * Replace Items with their Decaying forms for comparison.
     * @param stack
     * @param otherStack
     * @param cir
     */
    @WrapMethod(
        method = "isSameItemSameComponents"
    )
    private static boolean petrolpark$checkDecay(ItemStack stack, ItemStack otherStack, Operation<Boolean> original) {
        return original.call(stack, otherStack) || ItemHelper.equalIgnoringComponents(stack, otherStack);
    };

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
