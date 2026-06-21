package petrolpark.mc.library.core.scratch.classes;

import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public non-sealed interface IByteBufScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends ISyncedScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
    @Override
    public StreamCodec<ByteBuf, TYPE> streamCodec();
};
