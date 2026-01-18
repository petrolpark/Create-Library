package com.petrolpark.core.scratch.symbol.expression.world.block;

import static com.petrolpark.core.scratch.argument.ExpressionArgument.parameter;
import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.stringParameter;

import javax.annotation.Nullable;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.argument.IScratchParameter;
import com.petrolpark.core.scratch.environment.ILevelEnvironment;
import com.petrolpark.core.scratch.symbol.expression.TernaryExpressionType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStatePropertyExpression<TYPE, ARGUMENT extends IScratchArgument<ILevelEnvironment, TYPE>, EXPRESSION extends BlockStatePropertyExpression<TYPE, ARGUMENT, EXPRESSION>> extends TernaryExpressionType<
    ILevelEnvironment,
    TYPE,
    BlockPos, ExpressionArgument<ILevelEnvironment, BlockPos, ?>,
    String, ExpressionOrLiteralArgument<ILevelEnvironment, String>,
    TYPE, ARGUMENT,
    EXPRESSION
> {

    protected BlockStatePropertyExpression(IScratchParameter<ILevelEnvironment, TYPE, ARGUMENT> fallbackParameter) {
        super(parameter("position", PetrolparkScratchClasses.BLOCK_POS.get()), stringParameter("property"), fallbackParameter);
    };

    @Override
    public final TYPE evaluate(ILevelEnvironment environment, BlockPos pos, String property, TYPE fallback) {
        final TYPE value = getProperty(environment.getLevel().getBlockState(pos), property);
        return value == null ? fallback : value;
    };

    @Nullable
    public abstract TYPE getProperty(BlockState state, String property);

};
