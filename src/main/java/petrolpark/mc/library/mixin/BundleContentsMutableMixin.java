package petrolpark.mc.library.mixin;

import org.apache.commons.lang3.math.Fraction;
import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.item.component.BundleContents;
import petrolpark.mc.library.core.world.item.bundle.IExpandedBundleContentsMutable;

@Mixin(BundleContents.Mutable.class)
public class BundleContentsMutableMixin implements IExpandedBundleContentsMutable {
    
    @Unique
    protected Fraction size = Fraction.ONE;

    @Override
    public void setSize(Fraction size) {
        this.size = size;
    };

    @ModifyExpressionValue(
        method = "getMaxAmountToAdd",
        at = @At(
            value = "INVOKE",
            target = "subtract"
        )
    )
    public Fraction pquality$itemsWeighLessInQualityBundles(Fraction original) {
        return original.add(size).subtract(Fraction.ONE);
    };
};
