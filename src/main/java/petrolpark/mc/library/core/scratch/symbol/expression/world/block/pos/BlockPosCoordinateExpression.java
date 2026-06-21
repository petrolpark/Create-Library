package petrolpark.mc.library.core.scratch.symbol.expression.world.block.pos;

import static petrolpark.mc.library.core.scratch.argument.DropdownArgument.axisParameter;

import petrolpark.mc.library.core.scratch.argument.DropdownArgument;
import petrolpark.mc.library.core.scratch.argument.DropdownArgument.DropdownParameter;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import petrolpark.mc.library.core.scratch.classes.IntegerScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.BinaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;

public final class BlockPosCoordinateExpression extends BinaryExpressionType<
    IScratchEnvironment,
    Long,
    Axis, DropdownArgument<IScratchEnvironment, Axis>, DropdownParameter<IScratchEnvironment, Axis>,
    BlockPos, ExpressionArgument<IScratchEnvironment, BlockPos>, ExpressionParameter<IScratchEnvironment, BlockPos>,
    BlockPosCoordinateExpression
> {

    public BlockPosCoordinateExpression() {
        super(axisParameter("axis"), PetrolparkScratchClasses.BLOCK_POS.get().createDefaultParameter("pos"));
    };

    @Override
    public IntegerScratchClass getReturnClass() {
        return PetrolparkScratchClasses.INTEGER.get();
    };

    @Override
    public Long evaluate(IScratchEnvironment environment, Axis argument1, BlockPos argument2) {
        return (Long)(long)argument2.get(argument1);
    };

    @Override
    protected BlockPosCoordinateExpression self() {
        return this;
    };
    
};
