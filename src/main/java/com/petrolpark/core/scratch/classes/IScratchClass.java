package com.petrolpark.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.core.scratch.argument.IExpressionScratchParameter;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;
import com.petrolpark.registry.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> {
    
    /**
     * Use {@link IScratchClass#CODEC} instead.
     */
    static Codec<IScratchClass<?, ?>> TYPED_CODEC = PetrolparkRegistries.SCRATCH_CLASSES.byNameCodec().dispatch(IScratchClass::getType, IScratchClassType::scratchClassCodec);

    public static StreamCodec<RegistryFriendlyByteBuf, IScratchClass<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.SCRATCH_CLASS_TYPE).dispatch(IScratchClass::getType, IScratchClassType::scratchClassStreamCodec);

    public static Codec<IScratchClass<?, ?>> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    public Codec<TYPE> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, TYPE> streamCodec();

    public TYPE fallback();
    
    public IScratchClassType getType();

    public ISyncedScratchClass<TYPE, DEFAULT_ARGUMENT> asSynced();

    public <ENVIRONMENT extends IScratchEnvironment> IScratchParameter<ENVIRONMENT, TYPE, DEFAULT_ARGUMENT> createDefaultParameter(String key);

    public <TO_TYPE> Optional<IScratchClass.Caster<TYPE, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass);

    @FunctionalInterface
    public static interface Caster<FROM_TYPE, TO_TYPE> {

        public <ARGUMENT extends IScratchArgument<IScratchEnvironment, TO_TYPE>> ARGUMENT cast(IExpressionScratchParameter<IScratchEnvironment, TO_TYPE, ARGUMENT> toParameter, ExpressionAndArguments<IScratchEnvironment, FROM_TYPE, ?> fromExpressionAndArguments);
    };
};
