package petrolpark.mc.library.core.scratch.argument;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.IScratchContextProvider;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IScratchParameter<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>> {

    public String key();

    public ContextualCodec<IScratchContextProvider<?>, ARGUMENT> argumentCodec();

    public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ARGUMENT> argumentStreamCodec();
};
