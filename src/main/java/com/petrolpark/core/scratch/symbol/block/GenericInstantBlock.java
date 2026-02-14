package com.petrolpark.core.scratch.symbol.block;

import java.util.function.Function;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IGenericScratchSymbol;

import net.minecraft.network.RegistryFriendlyByteBuf;

public abstract class GenericInstantBlock<
    ENVIRONMENT extends IScratchEnvironment,
    GENERIC_TYPE,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends InstantScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IGenericScratchSymbol<ENVIRONMENT, GENERIC_TYPE, ARGUMENTS, PARAMETERS> {

    protected final IScratchClass<GENERIC_TYPE> genericScratchClass;

    protected GenericInstantBlock(IScratchClass<GENERIC_TYPE> genericClass, PARAMETERS parameters) {
        super(parameters);
        this.genericScratchClass = genericClass;
    };

    @Override
    public final IScratchClass<GENERIC_TYPE> getGenericScratchClass() {
        return genericScratchClass;
    };

    public static class Type<BLOCK extends GenericInstantBlock<?, ?, ?, ?>> implements IScratchBlock.Type<BLOCK> {

        protected final Function<IScratchClass<?>, BLOCK> factory;

        private final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec;
        private final ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec;

        public Type(Function<IScratchClass<?>, BLOCK> factory) {
            this.factory = factory;

            codec = RecordContextualCodecBuilder.mapCodec(instance -> instance.group(
                ContextualCodec.<IScratchEnvironment.Type<?>, IScratchClass<?>>of(IScratchClass.CODEC).fieldOf("class").forGetter(GenericInstantBlock::getGenericScratchClass)
            ).apply(instance, factory));

            streamCodec = ContextualStreamCodec.of(IScratchClass.STREAM_CODEC.map(factory, GenericInstantBlock::getGenericScratchClass));
        };

        @Override
        public ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec() {
            return streamCodec;
        };

    };
    
};
