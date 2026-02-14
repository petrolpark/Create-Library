package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.MissingExpression;
import com.petrolpark.core.scratch.symbol.expression.ScratchExpressionType;
import com.petrolpark.core.scratch.symbol.expression.list.ListElementExpression;
import com.petrolpark.core.scratch.symbol.expression.list.ListElementOrFallbackExpression;
import com.petrolpark.core.scratch.symbol.expression.list.ListLengthExpression;
import com.petrolpark.core.scratch.symbol.expression.list.ListOfExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.AndExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.ConditionalExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.EqualsExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.NotExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.OrExpression;
import com.petrolpark.core.scratch.symbol.expression.logic.XorExpression;
import com.petrolpark.core.scratch.symbol.expression.math.IntegerArithmeticExpression;
import com.petrolpark.core.scratch.symbol.expression.math.IntegerComparisonExpression;
import com.petrolpark.core.scratch.symbol.expression.variable.QueryVariableExpression;
import com.petrolpark.core.scratch.symbol.expression.world.block.pos.BlockPosCoordinateExpression;
import com.petrolpark.core.scratch.symbol.expression.world.block.pos.BlockPosExpression;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchExpressionTypes {

    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<MissingExpression<?>>> MISSING = REGISTRATE.genericScratchExpressionType("missing", MissingExpression::create);

    // List

    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<ListElementExpression<?>>> LIST_ELEMENT = REGISTRATE.genericScratchExpressionType("list_element", ListElementExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<ListElementOrFallbackExpression<?>>> LIST_ELEMENT_OR_FALLBACK = REGISTRATE.genericScratchExpressionType("list_element_or_fallback", ListElementOrFallbackExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<ListLengthExpression<?>>> LIST_LENGTH = REGISTRATE.genericScratchExpressionType("list_length", ListLengthExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, ScratchExpressionType<ListOfExpression<?, ?>>> LIST_OF = REGISTRATE.scratchExpressionType("list_of", ListOfExpression.CODEC, ListOfExpression.STREAM_CODEC);

    // Logic

    public static final RegistryEntry<IScratchExpression.Type<?>, AndExpression> AND = REGISTRATE.booleanScratchExpression("and", AndExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<ConditionalExpression<?>>> CONDITIONAL = REGISTRATE.genericScratchExpressionType("conditional", ConditionalExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<EqualsExpression<?>>> EQUALS = REGISTRATE.genericScratchExpressionType("equals", EqualsExpression::create);
    public static final RegistryEntry<IScratchExpression.Type<?>, NotExpression> NOT = REGISTRATE.booleanScratchExpression("not", NotExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, OrExpression> OR = REGISTRATE.booleanScratchExpression("or", OrExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, XorExpression> XOR = REGISTRATE.booleanScratchExpression("xor", XorExpression::new);

    // Variables

    public static final RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<QueryVariableExpression<?>>> QUERY = REGISTRATE.genericScratchExpressionType("query", QueryVariableExpression::create);

    // Math

    public static final RegistryEntry<IScratchExpression.Type<?>, IntegerArithmeticExpression> INTEGER_ARITHMETIC = REGISTRATE.scratchExpressionType("integer_arithmetic", IntegerArithmeticExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, IntegerComparisonExpression> INTEGER_COMPARISON = REGISTRATE.scratchExpressionType("integer_comparison", IntegerComparisonExpression::new);

    // World - Block - Pos

    public static final RegistryEntry<IScratchExpression.Type<?>, BlockPosCoordinateExpression> BLOCK_POS_COORDINATE = REGISTRATE.scratchExpressionType("block_pos_coordinate", BlockPosCoordinateExpression::new);
    public static final RegistryEntry<IScratchExpression.Type<?>, BlockPosExpression> BLOCK_POS= REGISTRATE.scratchExpressionType("block_pos", BlockPosExpression::new);
    
    
    public static final void register() {};
};
