package petrolpark.mc.library.core.scratch.classes;

import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import java.util.Optional;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class IntegerScratchClass extends SimpleParseableScratchClass<Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>> implements IByteBufScratchClass<Long, ExpressionOrLiteralArgument<IScratchEnvironment, Long>> {

    @Override
    public Long fallback() {
        return 0l;
    };

    @Override
    public Codec<Long> codec() {
        return Codec.LONG;
    };

    @Override
    public StreamCodec<ByteBuf, Long> streamCodec() {
        return ByteBufCodecs.VAR_LONG;
    };

    @Override
    public ExpressionOrLiteralParameter<IScratchEnvironment, Long> createDefaultParameter(String key) {
        return integerParameter(key);
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<Long, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };

    @Override
    public Long parse(String string) {
        // TODO Auto-generated method stub
        return 0l;
    };
    
};
