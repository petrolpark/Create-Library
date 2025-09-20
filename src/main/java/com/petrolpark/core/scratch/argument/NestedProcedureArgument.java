package com.petrolpark.core.scratch.argument;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.ScratchProcedure;
import com.petrolpark.core.scratch.symbol.block.NestedProcedureBlockInstance;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record NestedProcedureArgument<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> (
    ScratchProcedure<ENVIRONMENT> procedure,
    NestedProcedureParameter<ENVIRONMENT, INSTANCE> parameter,
    IScratchContextHolder<?> enclosingContextHolder
) implements IScratchArgument<ENVIRONMENT, ScratchProcedure<ENVIRONMENT>>, IScratchContextHolder<INSTANCE> {

    public static final <ENVIRONMENT extends IScratchEnvironment, INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> NestedProcedureParameter<ENVIRONMENT, INSTANCE> procedure(String key) {
        return new NestedProcedureParameter<>(key);
    };

    @Override
    public ScratchProcedure<ENVIRONMENT> get(ENVIRONMENT environment, IScratchContext<?> scope) {
        return procedure();
    };

    public static final class NestedProcedureParameter<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends NestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> implements IScratchParameter<ENVIRONMENT, ScratchProcedure<ENVIRONMENT>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> {

        private final String key;

        private final ContextualCodec<IScratchContextHolder<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> codec = RecordContextualCodecBuilder.create(instance -> instance.group(
            ScratchProcedure.<ENVIRONMENT>codec().fieldOf("procecure").forGetter(NestedProcedureArgument::procedure),
            instance.context()
        ).apply(instance, (procedure, contextHolder) -> new NestedProcedureArgument<>(procedure, this, contextHolder)));

        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> streamCodec = ScratchProcedure.<ENVIRONMENT>streamCodec().map((procedure, contextHolder) -> new NestedProcedureArgument<>(procedure, this, contextHolder), NestedProcedureArgument::procedure);

        public NestedProcedureParameter(String key) {
            this.key = key;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextHolder<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> codec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> streamCodec() {
            return streamCodec;
        };

    };
    
};
