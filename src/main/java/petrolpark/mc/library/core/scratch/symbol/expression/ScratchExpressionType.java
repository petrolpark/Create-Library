package petrolpark.mc.library.core.scratch.symbol.expression;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.util.codec.ContextualMapCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ScratchExpressionType<EXPRESSION extends IScratchExpression<?, ?, ?, ?>>(ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec, ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec) implements IScratchExpression.Type<EXPRESSION> {
    
    public ScratchExpressionType(MapCodec<EXPRESSION> codec, StreamCodec<? super RegistryFriendlyByteBuf, EXPRESSION> streamCodec) {
        this(ContextualMapCodec.of(codec), ContextualStreamCodec.of(streamCodec));
    };
};
