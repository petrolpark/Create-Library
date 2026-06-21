package petrolpark.mc.library.core.scratch.classes;

import java.util.function.Function;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

public abstract class GenericScratchClass<TYPE, GENERIC_TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> implements IScratchClass<TYPE, DEFAULT_ARGUMENT> {

    static <SCRATCH_CLASS extends GenericScratchClass<?, ?, ?>> Products.P1<RecordCodecBuilder.Mu<SCRATCH_CLASS>, ISyncedScratchClass<?, ?>> commonCodecFields(RecordCodecBuilder.Instance<SCRATCH_CLASS> instance) {
        return instance.group(IScratchClass.CODEC.<ISyncedScratchClass<?, ?>>xmap(IScratchClass::asSynced, Function.identity()).fieldOf("class").forGetter(GenericScratchClass::getGenericScratchClass));
    };
    
    protected final ISyncedScratchClass<GENERIC_TYPE,?> genericScratchClass;

    public GenericScratchClass(ISyncedScratchClass<GENERIC_TYPE, ?> genericScratchClass) {
        this.genericScratchClass = genericScratchClass;
    };

    public final ISyncedScratchClass<GENERIC_TYPE, ?> getGenericScratchClass() {
        return genericScratchClass;
    };
};
