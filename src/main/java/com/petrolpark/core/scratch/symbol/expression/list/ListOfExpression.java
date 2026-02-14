package com.petrolpark.core.scratch.symbol.expression.list;

import static com.petrolpark.core.scratch.ScratchParameters.parameters;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkScratchExpressionTypes;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.classes.ListScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.IGenericScratchSymbol;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.ScratchExpressionType;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ListOfExpression<TYPE, ARGUMENTS extends ScratchArguments<IScratchEnvironment, ?>> extends GenericExpression<IScratchEnvironment, TYPE, List<TYPE>, ARGUMENTS, ScratchParameters<IScratchEnvironment, ARGUMENTS>> {

    public static final MapCodec<ListOfExpression<?, ?>> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        IGenericScratchSymbol.commonCodecFields(instance)
        .and(Codec.intRange(1, 16).fieldOf("arguments").forGetter(ListOfExpression::arguments))
        .apply(instance, ListOfExpression::create)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ListOfExpression<?, ?>> STREAM_CODEC = StreamCodec.composite(
        IScratchClass.STREAM_CODEC, ListOfExpression::getGenericScratchClass,
        ByteBufCodecs.INT, ListOfExpression::arguments,
        ListOfExpression::create
    );

    protected final IScratchClass<List<TYPE>> listScratchClass;
    protected final int arguments;

    public static final <TYPE> ListOfExpression<TYPE, ?> create(IScratchClass<TYPE> scratchClass, int arguments) {
        ScratchParameters.Builder<IScratchEnvironment> parameters = ScratchParameters.parameters();
        for (int i = 0; i < arguments; i++) parameters = parameters.after(scratchClass.createDefaultParameter("value_"+i));
        return new ListOfExpression<>(scratchClass, parameters.build());
    };
    
    protected ListOfExpression(IScratchClass<TYPE> genericScratchClass, ScratchParameters<IScratchEnvironment, ARGUMENTS> parameters) {
        super(genericScratchClass, parameters);
        listScratchClass = ListScratchClass.create(genericScratchClass);
        arguments = (int)parameters().stream().count();
    };

    public int arguments() {
        return arguments;
    };

    @Override
    @SuppressWarnings("unchecked")
    public List<TYPE> evaluate(IScratchEnvironment environment, ARGUMENTS arguments) {
        return arguments.stream().map(argument -> argument.get(environment)).map(value -> (TYPE)value).toList();
    };

    @Override
    public IScratchClass<List<TYPE>> getReturnClass() {
        return listScratchClass;
    };

    @Override
    public ScratchExpressionType<ListOfExpression<?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_OF.get();
    };
    
};
