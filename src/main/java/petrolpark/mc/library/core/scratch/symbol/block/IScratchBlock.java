package petrolpark.mc.library.core.scratch.symbol.block;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.IScratchSymbol;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public sealed interface IScratchBlock<
    ENVIRONMENT extends IScratchEnvironment,
    ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>,
    PARAMETERS extends ScratchParameters<ENVIRONMENT, ARGUMENTS>
> extends IScratchSymbol<ENVIRONMENT, ARGUMENTS, PARAMETERS> permits IInstantiableScratchBlock, IInstantScratchBlock {

    /**
     * Use {@link #CODEC} instead.
     */
    static ContextualCodec<IScratchEnvironment.Type<?>, IScratchBlock<?, ?, ?>> TYPED_CODEC = ContextualCodec.dispatch(PetrolparkRegistries.SCRATCH_BLOCK_TYPES.byNameCodec(), IScratchBlock::getBlockType, IScratchBlock.Type::codec);

    public static ContextualCodec<IScratchEnvironment.Type<?>, IScratchBlock<?, ?, ?>> CODEC = ContextualCodec.lazyInitialized(() -> TYPED_CODEC);

    public static ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, IScratchBlock<?, ?, ?>> STREAM_CODEC = ContextualStreamCodec.dispatch(ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_BLOCK_TYPE), IScratchBlock::getBlockType, IScratchBlock.Type::streamCodec);

    public IScratchBlock.Type<?> getBlockType();

    public interface Type<BLOCK extends IScratchBlock<?, ?, ?>> extends IScratchSymbol.Type<BLOCK> {};
};
