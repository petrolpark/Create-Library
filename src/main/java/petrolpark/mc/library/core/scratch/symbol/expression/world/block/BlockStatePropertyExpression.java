package petrolpark.mc.library.core.scratch.symbol.expression.world.block;

import static petrolpark.mc.library.core.scratch.argument.ExpressionArgument.parameter;
import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.stringParameter;

import javax.annotation.Nullable;

import petrolpark.mc.library.core.scratch.argument.ExpressionArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
import petrolpark.mc.library.core.scratch.environment.ILevelEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.TernaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStatePropertyExpression<TYPE, ARGUMENT extends IScratchArgument<ILevelEnvironment, TYPE>, PARAMETER extends IScratchParameter<ILevelEnvironment, TYPE, ARGUMENT>, EXPRESSION extends BlockStatePropertyExpression<TYPE, ARGUMENT, PARAMETER, EXPRESSION>> extends TernaryExpressionType<
    ILevelEnvironment,
    TYPE,
    BlockPos, ExpressionArgument<ILevelEnvironment, BlockPos>, ExpressionParameter<ILevelEnvironment, BlockPos>,
    String, ExpressionOrLiteralArgument<ILevelEnvironment, String>, ExpressionOrLiteralParameter<ILevelEnvironment, String>,
    TYPE, ARGUMENT, PARAMETER,
    EXPRESSION
> {

    protected BlockStatePropertyExpression(PARAMETER fallbackParameter) {
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
