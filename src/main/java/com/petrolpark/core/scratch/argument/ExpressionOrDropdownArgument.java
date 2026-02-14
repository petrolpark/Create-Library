package com.petrolpark.core.scratch.argument;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.codec.RecordContextualCodecBuilder;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.procedure.IScratchContext;
import com.petrolpark.core.scratch.procedure.IScratchContextHolder;
import com.petrolpark.core.scratch.procedure.IScratchContextProvider;
import com.petrolpark.core.scratch.symbol.expression.ExpressionAndArguments;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public record ExpressionOrDropdownArgument<ENVIRONMENT extends IScratchEnvironment, TYPE> (
    int index,
    Optional<ExpressionArgument<ENVIRONMENT, TYPE>> expression,
    ExpressionOrDropdownParameter<ENVIRONMENT, TYPE> parameter
) implements IScratchArgument<ENVIRONMENT, TYPE>, IScratchContextHolder {

    public static final <ENVIRONMENT extends IScratchEnvironment> ExpressionOrDropdownParameter<ENVIRONMENT, Boolean> booleanParameter(String key) {
        return new ExpressionOrDropdownParameter<>(key, PetrolparkScratchClasses.BOOLEAN.get(), BooleanScratchClass.getValues());
    };

    @Override
    public TYPE get(ENVIRONMENT environment) {
        return expression().map(expression -> expression.get(environment)).orElse(parameter().values.get(index).value(environment));
    };

    @Override
    public boolean canEvaluate() {
        return expression().map(ExpressionArgument::canEvaluate).orElse(true);
    };

    @Override
    public <CONTEXT extends IScratchContext<CONTEXT>> void populateContext(IScratchContextProvider<CONTEXT> contextProvider, CONTEXT context) {
        expression().ifPresent(expression -> expression.populateContext(contextProvider, context));
    };

    public static final class ExpressionOrDropdownParameter<ENVIRONMENT extends IScratchEnvironment, TYPE> implements IExpressionScratchParameter<ENVIRONMENT, TYPE, ExpressionOrDropdownArgument<ENVIRONMENT, TYPE>> {

        protected final List<DropdownArgument.Entry<? super ENVIRONMENT, TYPE>> values;

        private final ExpressionParameter<ENVIRONMENT, TYPE> expressionParameter;
        private final ContextualCodec<IScratchContextProvider<?>, ExpressionOrDropdownArgument<ENVIRONMENT, TYPE>> codec;
        private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionOrDropdownArgument<ENVIRONMENT, TYPE>> streamCodec;

        public ExpressionOrDropdownParameter(String key, IScratchClass<TYPE> scratchClass, List<DropdownArgument.Entry<? super ENVIRONMENT, TYPE>> values) {
            this.values = values;
            expressionParameter = new ExpressionParameter<>(key, scratchClass);

            codec = RecordContextualCodecBuilder.create(instance -> instance.group(
                ContextualCodec.<IScratchContextProvider<?>, Integer>of(Codec.intRange(0, values.size() - 1)).fieldOf("index").forGetter(ExpressionOrDropdownArgument::index),
                expressionParameter.argumentCodec().optionalFieldOf("expression").forGetter(ExpressionOrDropdownArgument::expression)
            ).apply(instance, (index, expression) -> new ExpressionOrDropdownArgument<>(index, expression, this)));

            streamCodec = ContextualStreamCodec.composite(
                ContextualStreamCodec.of(ByteBufCodecs.INT), ExpressionOrDropdownArgument::index,
                ContextualStreamCodec.optional(expressionParameter.argumentStreamCodec()), ExpressionOrDropdownArgument::expression,
                (index, expression) -> new ExpressionOrDropdownArgument<>(index, expression, this)
            );
        };

        @Override
        public <ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> ExpressionOrDropdownArgument<ENVIRONMENT, TYPE> pass(ExpressionAndArguments<ENVIRONMENT, TYPE, ARGUMENTS> expressionAndArguments) {
            return new ExpressionOrDropdownArgument<>(0, Optional.of(new ExpressionArgument<>(expressionAndArguments, expressionParameter)), this);
        };

        @Override
        public String key() {
            return expressionParameter.key();
        };

        @Override
        public ContextualCodec<IScratchContextProvider<?>, ExpressionOrDropdownArgument<ENVIRONMENT, TYPE>> argumentCodec() {
            return codec;
        };

        @Override
        public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ExpressionOrDropdownArgument<ENVIRONMENT, TYPE>> argumentStreamCodec() {
            return streamCodec;
        };

    };
    
};
