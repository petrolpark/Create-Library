package petrolpark.mc.library.core.scratch.symbol.expression.world.block;

import static petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.booleanParameter;

import java.util.Map;

import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrDropdownArgument.ExpressionOrDropdownParameter;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.environment.ILevelEnvironment;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public final class BooleanBlockStatePropertyExpression extends BlockStatePropertyExpression<Boolean, ExpressionOrDropdownArgument<ILevelEnvironment, Boolean>, ExpressionOrDropdownParameter<ILevelEnvironment, Boolean>, BooleanBlockStatePropertyExpression> {

    protected BooleanBlockStatePropertyExpression() {
        super(booleanParameter("fallback"));
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
