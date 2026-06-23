package petrolpark.mc.library.mixin;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.neoforge.common.NeoForge;
import petrolpark.mc.library.core.world.item.bundle.BundleSizeEvent;
import petrolpark.mc.library.core.world.item.bundle.IExpandedBundleContentsMutable;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    
    @ModifyExpressionValue(
        method = "*",
        at = @At(
            value = "NEW",
            target = "Lnet/minecraft/world/item/component/BundleContents$Mutable;"
        )
    )
    public BundleContents.Mutable petrolpark$attachQuality(BundleContents.Mutable original, @Local(ordinal = 0) ItemStack stack) {
        ((IExpandedBundleContentsMutable)(original)).setSize(NeoForge.EVENT_BUS.post(new BundleSizeEvent(stack)).getSize());
        return original;
    };

    @ModifyExpressionValue(
        method = "getBarWidth",
        at = @At(
            value = "INVOKE",
            target = "weight"
        )
    )
    public Fraction petrolpark$scaleBarWidth(Fraction original, ItemStack stack) {
        return original.divideBy(NeoForge.EVENT_BUS.post(new BundleSizeEvent(stack)).getSize());
    };

    @WrapOperation(
        method = "appendHoverText",
        at = @At(
            value = "INVOKE",
            target = "translatable"
        )
    )
    public MutableComponent petrolpark$increaseDisplayedMaxContents(String string, Object[] args, Operation<MutableComponent> original, ItemStack stack) {
        return original.call(string, new Object[]{args[0], NeoForge.EVENT_BUS.post(new BundleSizeEvent(stack)).getSize().multiplyBy(Fraction.getFraction((int)args[1], 1)).intValue()});
    };
};
