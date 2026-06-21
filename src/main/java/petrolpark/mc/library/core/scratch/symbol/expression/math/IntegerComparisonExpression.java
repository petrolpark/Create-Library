package petrolpark.mc.library.core.scratch.symbol.expression.math;

import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import petrolpark.mc.library.core.scratch.argument.DropdownArgument;
import petrolpark.mc.library.core.scratch.argument.DropdownArgument.DropdownParameter;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.TernaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.util.Lang;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class IntegerComparisonExpression extends TernaryExpressionType<
    IScratchEnvironment,
    Boolean,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    IntegerComparisonExpression.Operation, DropdownArgument<IScratchEnvironment, IntegerComparisonExpression.Operation>, DropdownParameter<IScratchEnvironment, IntegerComparisonExpression.Operation>,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    IntegerComparisonExpression
> {
    
    public IntegerComparisonExpression() {
        super(integerParameter("integer1"), DropdownArgument.dropdownParameter("operation", IntegerComparisonExpression.Operation.values()), integerParameter("integer2"));
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public Boolean evaluate(IScratchEnvironment environment, Long argument1, Operation argument2, Long argument3) {
        return argument2.apply(argument1, argument3);
    };

    @Override
    protected IntegerComparisonExpression self() {
        return this;
    };

    public enum Operation implements DropdownArgument.Entry<IScratchEnvironment, IntegerComparisonExpression.Operation> {

        EQUAL {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 == (long)integer2;
            };
        },
        GREATER_THAN {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 > (long)integer2;
            };
        },
        LESS_THAN {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 < (long)integer2;
            };
        },
        GEQ {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 >= (long)integer2;
            };
        },
        LEQ {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 <= (long)integer2;
            };
        },
        NOT_EQUAL {
            @Override
            public Boolean apply(Long integer1, Long integer2) {
                return (long)integer1 != (long)integer2;
            };
        };

        protected final String translationKey;
        @OnlyIn(Dist.CLIENT)
        protected Component name;

        Operation() {
            translationKey = Lang.mathTranslationKey(Lang.asId(name()));
        };

        public abstract Boolean apply(Long integer1, Long integer2);

        @Override
        public Component name(IScratchEnvironment environment) {
            if (name == null) name = Component.translatable(translationKey);
            return name;
        };

        @Override
        public Operation value(IScratchEnvironment environment) {
            return this;
        };
    };
};
