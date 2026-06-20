package com.petrolpark.core.scratch.symbol.expression.world.block.pos;

import static com.petrolpark.core.scratch.argument.DropdownArgument.axisParameter;

import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.DropdownArgument.DropdownParameter;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import com.petrolpark.core.scratch.classes.IntegerScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.expression.BinaryExpressionType;
import com.petrolpark.registry.scratch.PetrolparkScratchClasses;

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
