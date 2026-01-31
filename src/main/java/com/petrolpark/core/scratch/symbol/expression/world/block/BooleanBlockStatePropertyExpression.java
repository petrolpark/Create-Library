package com.petrolpark.core.scratch.symbol.expression.world.block;

import static com.petrolpark.core.scratch.argument.ExpressionArgument.parameter;

import java.util.Map;

import com.petrolpark.PetrolparkScratchClasses;
import com.petrolpark.core.scratch.argument.ExpressionArgument;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.environment.ILevelEnvironment;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public final class BooleanBlockStatePropertyExpression extends BlockStatePropertyExpression<Boolean, ExpressionArgument<ILevelEnvironment, Boolean>, BooleanBlockStatePropertyExpression> {

    protected BooleanBlockStatePropertyExpression() {
        super(parameter("fallback", PetrolparkScratchClasses.BOOLEAN.get()));
    };

    @Override
    public BooleanScratchClass getReturnClass() {
        return PetrolparkScratchClasses.BOOLEAN.get();
    };

    @Override
    public Boolean getProperty(BlockState state, String property) {
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            if (entry.getKey() instanceof BooleanProperty booleanProperty && booleanProperty.getName().equals(property)) return booleanProperty.getValueClass().cast(entry.getValue());
        };
        return null;
    };

    @Override
    protected BooleanBlockStatePropertyExpression self() {
        return this;
    };
    
};
