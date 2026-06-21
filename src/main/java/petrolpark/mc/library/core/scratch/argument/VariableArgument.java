package petrolpark.mc.library.core.scratch.argument;

import com.mojang.serialization.DataResult;
import petrolpark.mc.library.core.scratch.environment.variable.IVariableScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.ScratchVariableIdentifier;
import petrolpark.mc.library.core.scratch.procedure.IScratchContextProvider;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record VariableArgument(ScratchVariableIdentifier identifier, VariableParameter parameter) implements IScratchArgument<IVariableScratchEnvironment, ScratchVariableIdentifier> {

    public static final VariableParameter variable(String key) {
        return new VariableParameter(key);
    };

    @Override
    public ScratchVariableIdentifier get(IVariableScratchEnvironment environment) {
        return identifier();
    };

    @Override
    public boolean canEvaluate() {
        //TODO
        return true;
    };

    public static class VariableParameter implements IScratchParameter<IVariableScratchEnvironment, ScratchVariableIdentifier, VariableArgument> {

        protected final String key;

        protected final ContextualCodec<IScratchContextProvider<?>, VariableArgument> argumentCodec = ContextualCodec.<IScratchContextProvider<?>, VariableArgument>of(ScratchVariableIdentifier.CODEC.xmap(identifier -> new VariableArgument(identifier, this), VariableArgument::identifier))
            .validate((contextProvider, argument) -> contextProvider.environmentType() instanceof IVariableScratchEnvironment.Type variableEnvironmentType && variableEnvironmentType.canAccessScope(argument.identifier().scope()) ? DataResult.success(argument) : DataResult.error(() -> "Scope not accessible in this Environment"));
        protected final ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextProvider<?>, VariableArgument> argumentStreamCodec = ContextualStreamCodec.of(ScratchVariableIdentifier.STREAM_CODEC.map((identifier) -> new VariableArgument(identifier, this), VariableArgument::identifier));

        public VariableParameter(String key) {
            this.key = key;
        };

        @Override
        public String key() {
            return key;
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, VariableArgument> argumentCodec() {
            return argumentCodec;
        };

        @Override
        public ContextualStreamCodec<RegistryFriendlyByteBuf, IScratchContextProvider<?>, VariableArgument> argumentStreamCodec() {
            return argumentStreamCodec();
        };

    };
    
};
