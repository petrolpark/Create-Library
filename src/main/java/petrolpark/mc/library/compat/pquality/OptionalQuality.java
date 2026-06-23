package petrolpark.mc.library.compat.pquality;

import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.util.function.ObjDouble2DoubleFunction;
import petrolpark.mc.library.util.function.ObjFloat2FloatFunction;
import petrolpark.mc.library.util.function.ObjInt2IntFunction;

public class OptionalQuality {

    static ObjDouble2DoubleFunction<IFlagPole<?, ?>> flagPoleDoubleMultiplier, flagPoleDoubleBigMultiplier, flagPoleDoubleReducer = (fp, b) -> b; 
    static ObjInt2IntFunction<IFlagPole<?, ?>> flagPoleIntMultiplier, flagPoleIntBigMultiplier, flagPoleIntReducer = (fp, b) -> b; 
    static ObjFloat2FloatFunction<IFlagPole<?, ?>> flagPoleFloatMultiplier, flagPoleFloatBigMultiplier, flagPoleFloatReducer = (fp, b) -> b;

    static ObjDouble2DoubleFunction<ItemStack> itemStackDoubleMultiplier, itemStackDoubleBigMultiplier, itemStackDoubleReducer = (fp, b) -> b; 
    static ObjInt2IntFunction<ItemStack> itemStackIntMultiplier, itemStackIntBigMultiplier, itemStackIntReducer = (fp, b) -> b; 
    static ObjFloat2FloatFunction<ItemStack> itemStackFloatMultiplier, itemStackFloatBigMultiplier, itemStackFloatReducer = (fp, b) -> b; 
  
    public static final double multiply(IFlagPole<?, ?> flagPole, double base) {
        return flagPoleDoubleMultiplier.apply(flagPole, base);
    };

    public static final double bigMultiply(IFlagPole<?, ?> flagPole, double base) {
        return flagPoleDoubleBigMultiplier.apply(flagPole, base);
    };

    public static final double reduce(IFlagPole<?, ?> flagPole, double base) {
        return flagPoleDoubleReducer.apply(flagPole, base);
    };

    public static final int multiply(IFlagPole<?, ?> flagPole, int base) {
        return flagPoleIntMultiplier.apply(flagPole, base);
    };

    public static final int bigMultiply(IFlagPole<?, ?> flagPole, int base) {
        return flagPoleIntBigMultiplier.apply(flagPole, base);
    };

    public static final int reduce(IFlagPole<?, ?> flagPole, int base) {
        return flagPoleIntReducer.apply(flagPole, base);
    };

    public static final float multiply(IFlagPole<?, ?> flagPole, float base) {
        return flagPoleFloatMultiplier.apply(flagPole, base);
    };

    public static final float bigMultiply(IFlagPole<?, ?> flagPole, float base) {
        return flagPoleFloatBigMultiplier.apply(flagPole, base);
    };

    public static final float reduce(IFlagPole<?, ?> flagPole, float base) {
        return flagPoleFloatReducer.apply(flagPole, base);
    };

    public static final double multiply(ItemStack itemStack, double base) {
        return itemStackDoubleMultiplier.apply(itemStack, base);
    };

    public static final double bigMultiply(ItemStack itemStack, double base) {
        return itemStackDoubleBigMultiplier.apply(itemStack, base);
    };

    public static final double reduce(ItemStack itemStack, double base) {
        return itemStackDoubleReducer.apply(itemStack, base);
    };

    public static final int multiply(ItemStack itemStack, int base) {
        return itemStackIntMultiplier.apply(itemStack, base);
    };

    public static final int bigMultiply(ItemStack itemStack, int base) {
        return itemStackIntBigMultiplier.apply(itemStack, base);
    };

    public static final int reduce(ItemStack itemStack, int base) {
        return itemStackIntReducer.apply(itemStack, base);
    };

    public static final float multiply(ItemStack itemStack, float base) {
        return itemStackFloatMultiplier.apply(itemStack, base);
    };

    public static final float bigMultiply(ItemStack itemStack, float base) {
        return itemStackFloatBigMultiplier.apply(itemStack, base);
    };

    public static final float reduce(ItemStack itemStack, float base) {
        return itemStackFloatReducer.apply(itemStack, base);
    };


};
