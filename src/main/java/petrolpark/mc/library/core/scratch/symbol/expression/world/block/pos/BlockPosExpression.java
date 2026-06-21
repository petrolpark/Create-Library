package petrolpark.mc.library.core.scratch.symbol.expression.world.block.pos;

import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.classes.BlockPosScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.TernaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

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
