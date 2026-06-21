package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.util.codec.ContextualMapCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import io.netty.buffer.ByteBuf;

public abstract class SimpleInstantBlockType<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>,
    BLOCK extends SimpleInstantBlockType<ENVIRONMENT, ARGUMENTS, PARAMETERS, ?>
> extends InstantScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> implements IScratchBlock.Type<BLOCK> {

    private final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec = ContextualMapCodec.unit(self());
    private final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec = ContextualStreamCodec.unit(self());
    
    protected SimpleInstantBlockType(PARAMETERS parameters) {
        super(parameters);
    };

    protected abstract BLOCK self();

    @Override
    public final ContextualMapCodec<IScratchEnvironment.Type<?>, BLOCK> codec() {
        return codec;
    };

    @Override
    public final ContextualStreamCodec<ByteBuf, IScratchEnvironment.Type<?>, BLOCK> streamCodec() {
        return streamCodec;
    };

    @Override
    public final IScratchBlock.Type<BLOCK> getBlockType() {
        return this;
    };
};
