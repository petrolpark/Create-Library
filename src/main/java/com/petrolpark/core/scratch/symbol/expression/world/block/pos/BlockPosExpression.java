package com.petrolpark.core.scratch.symbol.expression.world.block.pos;

import static com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument;
import com.petrolpark.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import com.petrolpark.core.scratch.classes.BlockPosScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.TernaryExpressionType;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;

import net.minecraft.core.BlockPos;

public final class BlockPosExpression extends TernaryExpressionType<
    IScratchEnvironment,
    BlockPos,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>, ExpressionOrLiteralParameter<IScratchEnvironment, Long>,
    BlockPosExpression
> {

    public BlockPosExpression() {
        super(integerParameter("x"), integerParameter("y"), integerParameter("z"));
    };

    @Override
    public BlockPosScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BLOCK_POS.get();
    };

    @Override
    public BlockPos evaluate(IScratchEnvironment environment, Long x, Long y, Long z) {
        return new BlockPos((int)(long)x, (int)(long)y, (int)(long)z);
    };

    @Override
    protected BlockPosExpression self() {
        return this;
    };
    
};
