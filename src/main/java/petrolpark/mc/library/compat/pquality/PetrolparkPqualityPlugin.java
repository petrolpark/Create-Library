package petrolpark.mc.library.compat.pquality;

import java.util.function.BiFunction;

import org.apache.commons.lang3.math.Fraction;

import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.flags.IFlagPole;
import petrolpark.mc.library.util.function.ObjDouble2DoubleFunction;
import petrolpark.mc.library.util.function.ObjFloat2FloatFunction;
import petrolpark.mc.library.util.function.ObjInt2IntFunction;
import petrolpark.mc.pquality.core.plugin.IPqualityPlugin;
import petrolpark.mc.pquality.core.plugin.PQualityPlugin;

@PQualityPlugin
public class PetrolparkPqualityPlugin implements IPqualityPlugin {
    
    @Override
    public void acceptFlagPoleModifiers(
        ObjDouble2DoubleFunction<IFlagPole<?, ?>> doubleMultiplier, ObjDouble2DoubleFunction<IFlagPole<?, ?>> doubleBigMultiplier, ObjDouble2DoubleFunction<IFlagPole<?, ?>> doubleReducer,
        ObjInt2IntFunction<IFlagPole<?, ?>> intMultiplier, ObjInt2IntFunction<IFlagPole<?, ?>> intBigMultiplier, ObjInt2IntFunction<IFlagPole<?, ?>> intReducer,
        ObjFloat2FloatFunction<IFlagPole<?, ?>> floatMultiplier, ObjFloat2FloatFunction<IFlagPole<?, ?>> floatBigMultiplier, ObjFloat2FloatFunction<IFlagPole<?, ?>> floatReducer,
        BiFunction<IFlagPole<?, ?>, Fraction, Fraction> fractionMultiplier, BiFunction<IFlagPole<?, ?>, Fraction, Fraction> fractionBigMultiplier, BiFunction<IFlagPole<?, ?>, Fraction, Fraction> fractionReducer
    ) {
        OptionalQuality.flagPoleDoubleMultiplier = doubleMultiplier;
        OptionalQuality.flagPoleDoubleBigMultiplier = doubleBigMultiplier;
        OptionalQuality.flagPoleDoubleReducer = doubleReducer;
        OptionalQuality.flagPoleIntMultiplier = intMultiplier;
        OptionalQuality.flagPoleIntBigMultiplier = intBigMultiplier;
        OptionalQuality.flagPoleIntReducer = intReducer;
        OptionalQuality.flagPoleFloatMultiplier = floatMultiplier;
        OptionalQuality.flagPoleFloatBigMultiplier = floatBigMultiplier;
        OptionalQuality.flagPoleFloatReducer = floatReducer;
        OptionalQuality.flagPoleFractionMultiplier = fractionMultiplier;
        OptionalQuality.flagPoleFractionBigMultiplier = fractionBigMultiplier;
        OptionalQuality.flagPoleFractionReducer = fractionReducer;
    };

    @Override
    public void acceptItemStackModifiers(
        ObjDouble2DoubleFunction<ItemStack> doubleMultiplier, ObjDouble2DoubleFunction<ItemStack> doubleBigMultiplier, ObjDouble2DoubleFunction<ItemStack> doubleReducer,
        ObjInt2IntFunction<ItemStack> intMultiplier, ObjInt2IntFunction<ItemStack> intBigMultiplier, ObjInt2IntFunction<ItemStack> intReducer,
        ObjFloat2FloatFunction<ItemStack> floatMultiplier, ObjFloat2FloatFunction<ItemStack> floatBigMultiplier, ObjFloat2FloatFunction<ItemStack> floatReducer,
        BiFunction<ItemStack, Fraction, Fraction> fractionMultiplier, BiFunction<ItemStack, Fraction, Fraction> fractionBigMultiplier, BiFunction<ItemStack, Fraction, Fraction> fractionReducer    
    ) {
        OptionalQuality.itemStackDoubleMultiplier = doubleMultiplier;
        OptionalQuality.itemStackDoubleBigMultiplier = doubleBigMultiplier;
        OptionalQuality.itemStackDoubleReducer = doubleReducer;
        OptionalQuality.itemStackIntMultiplier = intMultiplier;
        OptionalQuality.itemStackIntBigMultiplier = intBigMultiplier;
        OptionalQuality.itemStackIntReducer = intReducer;
        OptionalQuality.itemStackFloatMultiplier = floatMultiplier;
        OptionalQuality.itemStackFloatBigMultiplier = floatBigMultiplier;
        OptionalQuality.itemStackFloatReducer = floatReducer;
        OptionalQuality.itemStackFractionMultiplier = fractionMultiplier;
        OptionalQuality.itemStackFractionBigMultiplier = fractionBigMultiplier;
        OptionalQuality.itemStackFractionReducer = fractionReducer;
    };
};
