package petrolpark.mc.library.core.scratch.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionArgument.ExpressionParameter;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.expression.list.ListElementExpression;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed abstract class ListScratchClass<TYPE, BUFFER extends ByteBuf> extends GenericScratchClass<List<TYPE>, TYPE, ExpressionArgument<IScratchEnvironment, List<TYPE>>> {

    public static final MapCodec<ListScratchClass<?, ?>> CODEC = RecordCodecBuilder.mapCodec(instance -> commonCodecFields(instance).apply(instance, ListScratchClass::create));
    public static final StreamCodec<RegistryFriendlyByteBuf, ListScratchClass<?, ?>> STREAM_CODEC = StreamCodec.composite(IScratchClass.STREAM_CODEC, ListScratchClass::getGenericScratchClass, ListScratchClass::create);

    public static final <TYPE> ListScratchClass<TYPE, ?> create(IScratchClass<TYPE, ?> scratchClass) {
        return switch (scratchClass.asSynced()) {
            case IByteBufScratchClass<TYPE, ?> sc -> new ListByteBufScratchClass<>(sc);
            case IFriendlyByteBufScratchClass<TYPE, ?> sc -> new ListFriendlyByteBufScratchClass<>(sc);
            case IRegistryFriendlyByteBufScratchClass<TYPE, ?> sc -> new ListRegistryFriendlyByteBufScratchClass<>(sc);
        };
    };

    protected final Codec<List<TYPE>> codec;
    protected final StreamCodec<BUFFER, List<TYPE>> streamCodec;

    protected ListScratchClass(ISyncedScratchClass<TYPE, ?> genericScratchClass, StreamCodec<BUFFER, List<TYPE>> streamCodec) {
        super(genericScratchClass);
        codec = genericScratchClass.codec().listOf();
        this.streamCodec = streamCodec;
    };

    @Override
    public List<TYPE> fallback() {
        return new ArrayList<>();
    };
    
    @Override
    public Codec<List<TYPE>> codec() {
        return codec;
    };

    @Override
    public ExpressionParameter<IScratchEnvironment, List<TYPE>> createDefaultParameter(String key) {
        return ExpressionArgument.parameter(key, this);
    };

    @Override
    public <TO_TYPE> Optional<IScratchClass.Caster<List<TYPE>, TO_TYPE>> cast(IScratchClass<TO_TYPE, ?> toClass) {
        if (toClass.equals(getGenericScratchClass())) {
            return Optional.of(ListElementExpression.create(getGenericScratchClass())::withArgumentsUnchecked);
        } else if (toClass.equals(PetrolparkScratchClasses.INTEGER.get())) {
            
        };
        return Optional.empty();
    };
    
    @Override
    public ScratchClassType<ListScratchClass<?, ?>> getType() {
        return PetrolparkScratchClasses.LIST.get();
    };

    public static final class ListByteBufScratchClass<TYPE> extends ListScratchClass<TYPE, ByteBuf> implements IByteBufScratchClass<List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>> {

        protected ListByteBufScratchClass(IByteBufScratchClass<TYPE, ?> networkScratchClass) {
            super(networkScratchClass, networkScratchClass.streamCodec().apply(ByteBufCodecs.list()));
        };

        @Override
        public StreamCodec<io.netty.buffer.ByteBuf, List<TYPE>> streamCodec() {
            return streamCodec;
        };

    };

    public static final class ListFriendlyByteBufScratchClass<TYPE> extends ListScratchClass<TYPE, FriendlyByteBuf> implements IFriendlyByteBufScratchClass<List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>> {

        protected ListFriendlyByteBufScratchClass(IFriendlyByteBufScratchClass<TYPE, ?> networkScratchClass) {
            super(networkScratchClass, networkScratchClass.streamCodec().apply(ByteBufCodecs.list()));
        };

        @Override
        public StreamCodec<FriendlyByteBuf, List<TYPE>> streamCodec() {
            return streamCodec;
        };

    };

    public static final class ListRegistryFriendlyByteBufScratchClass<TYPE> extends ListScratchClass<TYPE, RegistryFriendlyByteBuf> implements IRegistryFriendlyByteBufScratchClass<List<TYPE>, ExpressionArgument<IScratchEnvironment, List<TYPE>>> {

        protected ListRegistryFriendlyByteBufScratchClass(IRegistryFriendlyByteBufScratchClass<TYPE, ?> networkScratchClass) {
            super(networkScratchClass, networkScratchClass.streamCodec().apply(ByteBufCodecs.list()));
        };

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, List<TYPE>> streamCodec() {
            return streamCodec;
        };

    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof ListScratchClass<?, ?> otherClass && getGenericScratchClass().equals(otherClass.getGenericScratchClass());
    };

};
