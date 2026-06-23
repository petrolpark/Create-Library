package petrolpark.mc.library.compat.pquality;

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
        ObjFloat2FloatFunction<IFlagPole<?, ?>> floatMultiplier, ObjFloat2FloatFunction<IFlagPole<?, ?>> floatBigMultiplier, ObjFloat2FloatFunction<IFlagPole<?, ?>> floatReducer
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
    };

    @Override
    public void acceptItemStackModifiers(
        ObjDouble2DoubleFunction<ItemStack> doubleMultiplier, ObjDouble2DoubleFunction<ItemStack> doubleBigMultiplier, ObjDouble2DoubleFunction<ItemStack> doubleReducer,
        ObjInt2IntFunction<ItemStack> intMultiplier, ObjInt2IntFunction<ItemStack> intBigMultiplier, ObjInt2IntFunction<ItemStack> intReducer,
        ObjFloat2FloatFunction<ItemStack> floatMultiplier, ObjFloat2FloatFunction<ItemStack> floatBigMultiplier, ObjFloat2FloatFunction<ItemStack> floatReducer
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
    };
};
