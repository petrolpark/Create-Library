package petrolpark.mc.library.core.scratch.classes;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface IRegistryFriendlyByteBufScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends ISyncedScratchClass<TYPE, DEFAULT_ARGUMENT> {
  
    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TYPE> streamCodec();
};
