package petrolpark.mc.library.core.scratch.symbol.block;

import javax.annotation.Nullable;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

public non-sealed interface IInstantiableScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>,
    INSTANCE extends IScratchBlockInstance<ENVIRONMENT>
> extends IScratchBlock<ENVIRONMENT, ARGUMENTS, PARAMETERS> {
    
    @Nullable
    public INSTANCE run(ENVIRONMENT environment, ARGUMENTS arguments);

    public ContextualCodec<ARGUMENTS, INSTANCE> instanceCodec();

    public ContextualStreamCodec<? super RegistryFriendlyByteBuf, ARGUMENTS, INSTANCE> instanceStreamCodec();
};
