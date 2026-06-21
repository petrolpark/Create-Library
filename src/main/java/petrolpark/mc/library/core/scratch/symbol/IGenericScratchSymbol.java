package petrolpark.mc.library.core.scratch.symbol;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.RecordContextualCodecBuilder;

public interface IGenericScratchSymbol<ENVIRONMENT extends IScratchEnvironment, GENERIC_TYPE, GENERIC_ARGUMENT extends IScratchArgument<IScratchEnvironment, GENERIC_TYPE>, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>, PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    static <SYMBOL extends IGenericScratchSymbol<?, ?, ?, ?, ?>> Products.P1<RecordCodecBuilder.Mu<SYMBOL>, IScratchClass<?, ?>> commonCodecFields(RecordCodecBuilder.Instance<SYMBOL> instance) {
        return instance.group(IScratchClass.CODEC.fieldOf("class").forGetter(IGenericScratchSymbol::getGenericScratchClass));
    };

    static <CONTEXT, SYMBOL extends IGenericScratchSymbol<?, ?, ?, ?, ?>> Products.P1<RecordContextualCodecBuilder.Mu<CONTEXT, SYMBOL>, IScratchClass<?, ?>> commonContextualCodecFields(RecordContextualCodecBuilder.Instance<CONTEXT, SYMBOL> instance) {
        return instance.group(ContextualCodec.<CONTEXT, IScratchClass<?, ?>>of(IScratchClass.CODEC).fieldOf("class").forGetter(IGenericScratchSymbol::getGenericScratchClass));
    };

    public IScratchClass<GENERIC_TYPE, GENERIC_ARGUMENT> getGenericScratchClass();

    @Override
    public default boolean canEvaluate(ARGUMENTS arguments) {
        return getGenericScratchClass() != PetrolparkScratchClasses.NULL.get();
    };
};
