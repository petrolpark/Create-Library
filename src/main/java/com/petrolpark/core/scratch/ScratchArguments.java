package com.petrolpark.core.scratch;

import java.util.function.Function;

import com.petrolpark.core.scratch.context.IScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.nullary.NullaryScratchExpression;

public class ScratchArguments<CONTEXT extends IScratchContext> implements ScratchParameters {
    
    protected final CONTEXT context;

    public ScratchArguments(CONTEXT context) {
        this.context = context;
    };

    public ScratchArguments<CONTEXT> contextOnly() {
        return new ScratchArguments<>(context);
    };

    public static class Builder<CONTEXT extends IScratchContext> extends ScratchArguments<CONTEXT> {

        protected Builder(CONTEXT context) {
            super(context);
        };

        public <TYPE_1> ScratchArguments1.Builder<CONTEXT, TYPE_1, ScratchParameters> with(NullaryScratchExpression<? super CONTEXT, TYPE_1> nullaryExpression) {
            return new ScratchArguments1.Builder<>(context, nullaryExpression, contextOnly());
        };

        public <TYPE_1, PARAMETERS_1 extends ScratchParameters> ScratchArguments1.Builder<CONTEXT, TYPE_1, PARAMETERS_1> with(IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression, Function<ScratchParameters, PARAMETERS_1> argumentBuilder) {
            return new ScratchArguments1.Builder<>(context, expression, argumentBuilder.apply(contextOnly()));
        };

    };

    public static class ScratchArguments1<
        CONTEXT extends IScratchContext,
        TYPE_1, PARAMETERS_1 extends ScratchParameters
    > extends ScratchArguments<
        CONTEXT
    > implements ScratchParameters1<
        TYPE_1
    > {
        protected final IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1;
        protected final PARAMETERS_1 arguments1;

        public ScratchArguments1(CONTEXT context, IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1) {
            super(context);
            this.expression1 = expression1;
            this.arguments1 = arguments1;
        };

        @Override
        public final TYPE_1 get1() {
            return expression1.evaluate(context, arguments1);
        };

        public static class Builder<
            CONTEXT extends IScratchContext,
            TYPE_1, PARAMETERS_1 extends ScratchParameters
        > extends ScratchArguments1<
            CONTEXT,
            TYPE_1, PARAMETERS_1
        > {
            protected Builder(CONTEXT context, IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1) {
                super(context, expression1, arguments1);
            };

            public <TYPE_2> ScratchArguments2.Builder<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, ScratchParameters> with(NullaryScratchExpression<? super CONTEXT, TYPE_2> nullaryExpression) {
                return new ScratchArguments2.Builder<>(context, expression1, arguments1, nullaryExpression, contextOnly());
            };

            public <TYPE_2, PARAMETERS_2 extends ScratchParameters> ScratchArguments2.Builder<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, PARAMETERS_2> with(IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression, Function<ScratchParameters, PARAMETERS_2> argumentBuilder) {
                return new ScratchArguments2.Builder<>(context, expression1, arguments1, expression, argumentBuilder.apply(contextOnly()));
            };

            public ScratchArguments1<CONTEXT, TYPE_1, PARAMETERS_1> finish() {
                return new ScratchArguments1<>(context, expression1, arguments1);
            };
        };
    };

    public static class ScratchArguments2<
        CONTEXT extends IScratchContext,
        TYPE_1, PARAMETERS_1 extends ScratchParameters,
        TYPE_2, PARAMETERS_2 extends ScratchParameters
    > extends ScratchArguments1<
        CONTEXT,
        TYPE_1, PARAMETERS_1
    > implements ScratchParameters2<
        TYPE_1,
        TYPE_2
    > {
        protected final IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression2;
        protected final PARAMETERS_2 arguments2;
        
        public ScratchArguments2(
            CONTEXT context,
            IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1,
            IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression2, PARAMETERS_2 arguments2
        ) {
            super(context, expression1, arguments1);
            this.expression2 = expression2;
            this.arguments2 = arguments2;
        };

        @Override
        public final TYPE_2 get2() {
            return expression2.evaluate(context, arguments2);
        };

        public static class Builder<
            CONTEXT extends IScratchContext,
            TYPE_1, PARAMETERS_1 extends ScratchParameters,
            TYPE_2, PARAMETERS_2 extends ScratchParameters
        > extends ScratchArguments2<
            CONTEXT,
            TYPE_1, PARAMETERS_1,
            TYPE_2, PARAMETERS_2
        > {
            protected Builder(
                CONTEXT context,
                IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1,
                IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression2, PARAMETERS_2 arguments2
            ) {
                super(context, expression1, arguments1, expression2, arguments2);
            };

            public <TYPE_3> ScratchArguments3.Builder<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, PARAMETERS_2, TYPE_3, ScratchParameters> with(NullaryScratchExpression<? super CONTEXT, TYPE_3> nullaryExpression) {
                return new ScratchArguments3.Builder<>(context, expression1, arguments1, expression2, arguments2, nullaryExpression, contextOnly());
            };

            public <TYPE_3, PARAMETERS_3 extends ScratchParameters> ScratchArguments2<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, PARAMETERS_2> with(IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression, Function<ScratchParameters, PARAMETERS_2> argumentBuilder) {
                return new ScratchArguments3.Builder<>(context, expression1, arguments1, expression2, arguments2, expression, argumentBuilder.apply(contextOnly()));
            };

            public ScratchArguments2<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, PARAMETERS_2> finish() {
                return new ScratchArguments2<>(context, expression1, arguments1, expression2, arguments2);
            };
        };
    };

    public static class ScratchArguments3<
        CONTEXT extends IScratchContext,
        TYPE_1, PARAMETERS_1 extends ScratchParameters,
        TYPE_2, PARAMETERS_2 extends ScratchParameters,
        TYPE_3, PARAMETERS_3 extends ScratchParameters
    > extends ScratchArguments2<
        CONTEXT,
        TYPE_1, PARAMETERS_1,
        TYPE_2, PARAMETERS_2
    > implements ScratchParameters3<
        TYPE_1,
        TYPE_2,
        TYPE_3
    > {
        protected final IScratchExpression<? super CONTEXT, TYPE_3, PARAMETERS_3> expression3;
        protected final PARAMETERS_3 arguments3;
        
        public ScratchArguments3(
            CONTEXT context,
            IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1,
            IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression2, PARAMETERS_2 arguments2,
            IScratchExpression<? super CONTEXT, TYPE_3, PARAMETERS_3> expression3, PARAMETERS_3 arguments3
        ) {
            super(context, expression1, arguments1, expression2, arguments2);
            this.expression3 = expression3;
            this.arguments3 = arguments3;
        };

        @Override
        public final TYPE_3 get3() {
            return expression3.evaluate(context, arguments3);
        };

        public static class Builder<
            CONTEXT extends IScratchContext,
            TYPE_1, PARAMETERS_1 extends ScratchParameters,
            TYPE_2, PARAMETERS_2 extends ScratchParameters,
            TYPE_3, PARAMETERS_3 extends ScratchParameters
        > extends ScratchArguments3<
            CONTEXT,
            TYPE_1, PARAMETERS_1,
            TYPE_2, PARAMETERS_2,
            TYPE_3, PARAMETERS_3
        > {
            protected Builder(CONTEXT context,
                IScratchExpression<? super CONTEXT, TYPE_1, PARAMETERS_1> expression1, PARAMETERS_1 arguments1,
                IScratchExpression<? super CONTEXT, TYPE_2, PARAMETERS_2> expression2, PARAMETERS_2 arguments2,
                IScratchExpression<? super CONTEXT, TYPE_3, PARAMETERS_3> expression3, PARAMETERS_3 arguments3
            ) {
                super(context, expression1, arguments1, expression2, arguments2, expression3, arguments3);
            };

            public ScratchArguments3<CONTEXT, TYPE_1, PARAMETERS_1, TYPE_2, PARAMETERS_2, TYPE_3, PARAMETERS_3> finish() {
                return new ScratchArguments3<>(context, expression1, arguments1, expression2, arguments2, expression3, arguments3);
            };
        };
    };
};
