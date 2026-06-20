package com.petrolpark.core.scratch.symbol.expression.math;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.DropdownArgument.DropdownParameter;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.TernaryExpressionType;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;
import com.petrolpark.util.Lang;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class IntegerArithmeticExpression extends TernaryExpressionType<
    IScratchEnvironment,
    Long,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    IntegerArithmeticExpression.Operation, DropdownArgument<IScratchEnvironment, IntegerArithmeticExpression.Operation>, DropdownParameter<IScratchEnvironment, IntegerArithmeticExpression.Operation>,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    IntegerArithmeticExpression
> {
    
    public IntegerArithmeticExpression() {
        super(integerParameter("integer1"), DropdownArgument.dropdownParameter("operation", IntegerArithmeticExpression.Operation.values()), integerParameter("integer2"));
    };

    @Override
    public IntegerScratchClass getReturnClass() {
        return PetrolparkScratchClasses.INTEGER.get();
    };

    @Override
    public Long evaluate(IScratchEnvironment environment, Long argument1, Operation argument2, Long argument3) {
        return argument2.apply(argument1, argument3);
    };

    @Override
    protected IntegerArithmeticExpression self() {
        return this;
    };

    public enum Operation implements DropdownArgument.Entry<IScratchEnvironment, IntegerArithmeticExpression.Operation> {

        ADD {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 + integer2;
            };
        },
        SUBTRACT {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 - integer2;
            };
        },
        MULTIPLY {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 * integer2;
            };
        },
        DIV {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 / integer2;
            };
        },
        MOD {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 % integer2;
            };
        },
        AND {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 & integer2;
            };
        },
        OR {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 | integer2;
            };
        },
        XOR {
            @Override
            public Long apply(Long integer1, Long integer2) {
                return integer1 ^ integer2;
            };
        }
        ;
        
        protected final String translationKey;
        @OnlyIn(Dist.CLIENT)
        protected Component name;

        Operation() {
            translationKey = Lang.mathTranslationKey(Lang.asId(name()));
        };

        public abstract Long apply(Long integer1, Long integer2);

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
