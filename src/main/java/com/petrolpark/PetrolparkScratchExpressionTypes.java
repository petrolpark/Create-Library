package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.MissingExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.AndExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.EqualsExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.NotExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.OrExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.XorExpression;
import com.petrolpark.core.scratch.symbol.expression.math.IntegerArithmeticExpression;
import com.petrolpark.core.scratch.symbol.expression.math.IntegerComparisonExpression;
import com.petrolpark.core.scratch.symbol.expression.variable.QueryListExpression;
import com.petrolpark.core.scratch.symbol.expression.variable.QueryVariableExpression;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchExpressionTypes {

    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<MissingExpression<?>>> MISSING = REGISTRATE.genericScratchExpressionType("missing", MissingExpression::create);

    // Logic

    public static final RegistryEntry<IScratchExpression.Type<?>, AndExpression> AND = REGISTRATE.booleanScratchExpression("and", AndExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<EqualsExpression<?>>> EQUALS = REGISTRATE.genericScratchExpressionType("equals", EqualsExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, NotExpression> NOT = REGISTRATE.booleanScratchExpression("not", NotExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, OrExpression> OR = REGISTRATE.booleanScratchExpression("or", OrExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, XorExpression> XOR = REGISTRATE.booleanScratchExpression("xor", XorExpression::new);

    // Variables

    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<QueryListExpression<?>>> QUERY_LIST = REGISTRATE.genericScratchExpressionType("query_list", QueryListExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<QueryVariableExpression<?>>> QUERY = REGISTRATE.genericScratchExpressionType("query", QueryVariableExpression::create);

    // Math

    public static final RegistryEntry<IScratchExpression.Type<?>, IntegerArithmeticExpression> INTEGER_ARITHMETIC = REGISTRATE.scratchExpressionType("integer_arithmetic", IntegerArithmeticExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, IntegerComparisonExpression> INTEGER_COMPARISON = REGISTRATE.scratchExpressionType("integer_comparison", IntegerComparisonExpression::new);
    
    public static final void register() {};
};
