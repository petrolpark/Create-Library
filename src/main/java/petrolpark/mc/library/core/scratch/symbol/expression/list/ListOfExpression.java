package petrolpark.mc.library.core.scratch.symbol.expression.list;

import static petrolpark.mc.library.core.scratch.ScratchParameters.parameters;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.ScratchParameters;
import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.classes.ListScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.IGenericScratchSymbol;
import petrolpark.mc.library.core.scratch.symbol.expression.GenericExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.ScratchExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchExpressionTypes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ListOfExpression<TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>, ARGUMENTS extends ScratchArguments<IScratchEnvironment, ?>> extends GenericExpression<IScratchEnvironment, TYPE, ARGUMENT, List<TYPE>, ARGUMENTS, ScratchParameters<IScratchEnvironment, ARGUMENTS>> {

    public static final MapCodec<ListOfExpression<?, ?, ?>> CODEC = RecordCodecBuilder.<ListOfExpression<?, ?, ?>>mapCodec(instance -> 
        IGenericScratchSymbol.commonCodecFields(instance)
        .and(Codec.intRange(1, 16).fieldOf("arguments").forGetter(ListOfExpression::arguments))
        .apply(instance, ListOfExpression::create)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ListOfExpression<?, ?, ?>> STREAM_CODEC = StreamCodec.composite(
        IScratchClass.STREAM_CODEC, ListOfExpression::getGenericScratchClass,
        ByteBufCodecs.INT, ListOfExpression::arguments,
        ListOfExpression::create
    );

    protected final ListScratchClass<TYPE, ?> listScratchClass;
    protected final int arguments;

    public static final <TYPE, ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> ListOfExpression<TYPE, ARGUMENT, ?> create(IScratchClass<TYPE, ARGUMENT> scratchClass, int arguments) {
        ScratchParameters.Builder<IScratchEnvironment> parameters = ScratchParameters.parameters();
        for (int i = 0; i < arguments; i++) parameters = parameters.after(scratchClass.createDefaultParameter("value_"+i));
        return new ListOfExpression<>(scratchClass, parameters.build());
    };
    
    protected ListOfExpression(IScratchClass<TYPE, ARGUMENT> genericScratchClass, ScratchParameters<IScratchEnvironment, ARGUMENTS> parameters) {
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
    public ListScratchClass<TYPE, ?> getReturnClass() {
        return listScratchClass;
    };

    @Override
    public ScratchExpressionType<ListOfExpression<?, ?, ?>> getExpressionType() {
        return PetrolparkScratchExpressionTypes.LIST_OF.get();
    };
    
};
