package petrolpark.mc.library.core.scratch.classes;

import java.util.Optional;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StringScratchClass extends SimpleParseableScratchClass<String, ExpressionOrLiteralArgument<IScratchEnvironment, String>> implements IByteBufScratchClass<String, ExpressionOrLiteralArgument<IScratchEnvironment, String>> {

    @Override
    public String fallback() {
        return "";
    };

    @Override
    public Codec<String> codec() {
        return Codec.STRING;
    };

    @Override
    public StreamCodec<ByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8;
    };

    @Override
    public ExpressionOrLiteralParameter<IScratchEnvironment, String> createDefaultParameter(String key) {
        return ExpressionOrLiteralArgument.stringParameter(key);
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<String, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cast'");
    };

    @Override
    public String parse(String string) {
        return string;
    };
    
};
