package com.petrolpark.core.scratch;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> {
    
    static Codec<IScratchClass<?, ?>> CODEC = PetrolparkRegistries.SCRATCH_CLASSES.byNameCodec();

    public Codec<TYPE> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, TYPE> streamCodec();

    public IScratchParameter<IScratchEnvironment, TYPE, DEFAULT_ARGUMENT> createDefaultParameter(String key);

    public <ENVIRONMENT extends IScratchEnvironment, TO_TYPE> Optional<Caster<ENVIRONMENT, TYPE, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass);

    public static interface Caster<ENVIRONMENT extends IScratchEnvironment, FROM_TYPE, TO_TYPE> {

        public ExpressionArgument<? super ENVIRONMENT, TO_TYPE, ?> cast(ExpressionArgument<? super ENVIRONMENT, FROM_TYPE, ?> expression);
    };
};
