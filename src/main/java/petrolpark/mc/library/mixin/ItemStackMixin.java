package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.core.world.item.IItemStackDuck;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;
import petrolpark.mc.library.util.ItemHelper;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IItemStackDuck {

    @Unique
    private IFlagPole<?, ?> flags;

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
    public IFlagPole<?, ?> getFlags() {
        if (flags == null) flags = ItemFlagPole.create(self());
        return flags;
    };

    @Override
    public void onFlagsSaved() {
        flags = null;
    };

    private ItemStack self() {
        return (ItemStack)(Object)this;  
    };
};
