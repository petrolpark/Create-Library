package petrolpark.mc.library.core.scratch.argument;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.IScratchContextProvider;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;
import petrolpark.mc.library.core.scratch.symbol.block.instance.INestedProcedureBlockInstance;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;
import petrolpark.mc.library.util.codec.RecordContextualCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record NestedProcedureArgument<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> (
    ScratchProcedure<ENVIRONMENT, INSTANCE> procedure,
    NestedProcedureParameter<ENVIRONMENT, INSTANCE> parameter
) implements IScratchArgument<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>> {

    public static final <ENVIRONMENT extends IScratchEnvironment, INSTANCE extends INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> NestedProcedureParameter<ENVIRONMENT, INSTANCE> procedure(String key) {
        return new NestedProcedureParameter<>(key);
    };

    @Override
    public ScratchProcedure<ENVIRONMENT, INSTANCE> get(ENVIRONMENT environment) {
        return procedure();
    };

    @Override
    public boolean canEvaluate() {
        return procedure().canRun();
    };

    public static final class NestedProcedureParameter<ENVIRONMENT extends IScratchEnvironment, INSTANCE extends INestedProcedureBlockInstance<ENVIRONMENT, INSTANCE>> implements IScratchParameter<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, INSTANCE>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> {

        private final String key;

        private final ContextualCodec<IScratchContextProvider<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> codec = RecordContextualCodecBuilder.create(instance -> instance.group(
            ScratchProcedure.<ENVIRONMENT, INSTANCE>codec().fieldOf("procecure").forGetter(NestedProcedureArgument::procedure)
        ).apply(instance, (procedure) -> new NestedProcedureArgument<>(procedure, this)));

        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> streamCodec = ScratchProcedure.<ENVIRONMENT, INSTANCE>streamCodec().map((procedure) -> new NestedProcedureArgument<>(procedure, this), NestedProcedureArgument::procedure);

        public NestedProcedureParameter(String key) {
            this.key = key;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, NestedProcedureArgument<ENVIRONMENT, INSTANCE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
    
};
