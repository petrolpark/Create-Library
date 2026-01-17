package com.petrolpark.core.scratch.procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.block.IInstantScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IInstantiableScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ScratchProcedure<ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> implements IScratchContextProvider<CONTEXT> {

    protected final IScratchContextProvider<?> enclosingContextProvider;

    public ScratchProcedure(IScratchContextProvider<?> enclosingContextProvider) {
        this.enclosingContextProvider = enclosingContextProvider;
    };

    protected final List<ScratchProcedure.Line<ENVIRONMENT, ?>> lines = new ArrayList<>();
    
    protected int currentLineNumber = 0;
    protected ScratchProcedure.CurrentLine<ENVIRONMENT, ?, ?> currentLine = null;

    public boolean tick(ENVIRONMENT environment) {
        while (currentLine == null || currentLine.tick(environment)) {
            currentLine = null;
            if (currentLineNumber < 0 || currentLineNumber >= lines.size()) return true; // Finished
            currentLineNumber++;
            currentLine = lines.get(currentLineNumber).run(environment);
        };
        return false;
    };

    public boolean canRun() {
        return !lines.stream().allMatch(ScratchProcedure.Line::canRun);
    };

    public void populateContext(CONTEXT context) {
        for (ScratchProcedure.Line<?, ?> line : lines) line.arguments().populateContext(this, context);
    };

    public void exit() {
        currentLineNumber = lines.size();
        currentLine = null;
    };

    @Override
    public IScratchEnvironment.Type<?> environmentType() {
        return enclosingContextProvider().environmentType();
    };

    @Override
    public IScratchContextProvider<?> enclosingContextProvider() {
        return enclosingContextProvider;
    };

    public static record Line<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>>(IScratchBlock<? super ENVIRONMENT, ARGUMENTS> block, ARGUMENTS arguments) {

        @Nullable
        public ScratchProcedure.CurrentLine<ENVIRONMENT, ARGUMENTS, ?> run(ENVIRONMENT environment) {
            if (block() instanceof final IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, ?> instantiableBlock) return ScratchProcedure.CurrentLine.fromInstantiable(instantiableBlock, arguments(), environment);
            else if (block() instanceof final IInstantScratchBlock<? super ENVIRONMENT, ARGUMENTS> instantBlock) instantBlock.run(environment, arguments());
            return null;
        };

        public boolean canRun() {
            return block().canEvaluate(arguments());
        };

    };

    public static record CurrentLine<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>>(IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, INSTANCE> block, ARGUMENTS arguments, INSTANCE instance) {

        public static <ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>> CurrentLine<ENVIRONMENT, ARGUMENTS, INSTANCE> fromInstantiable(IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, INSTANCE> block, ARGUMENTS arguments, ENVIRONMENT environment) {
            final INSTANCE instance = block.run(environment, arguments);
            if (instance == null) return null;
            return new CurrentLine<>(block, arguments, instance);
        };

        public boolean tick(ENVIRONMENT environment) {
            return instance().tick(environment);  
        };
    };

    public static final <ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> ContextualCodec<IScratchContextProvider<?>, ScratchProcedure<ENVIRONMENT, CONTEXT>> codec() {
        final ContextualMapCodec<IScratchContextProvider<?>, ScratchProcedure<ENVIRONMENT, CONTEXT>> mapCodec = new ContextualMapCodec<>() {

            private static final String LINES_KEY = "lines";
            private static final String CURRENT_LINE_KEY = "current_line";
            private static final String CURRENT_INSTANCE_KEY = "current_instance";

            @Override
            public <T> DataResult<ScratchProcedure<ENVIRONMENT, CONTEXT>> decode(final DynamicOps<T> ops, final IScratchContextProvider<?> contextProvider, final MapLike<T> input) {
                final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure = new ScratchProcedure<>(contextProvider);
                final DataResult<List<ScratchProcedure.Line<ENVIRONMENT, ?>>> lines = procedure.linesCodec.parse(ops, input.get(LINES_KEY));
                if (lines.isError()) return lines.map(l -> procedure).setPartial(procedure);
                procedure.lines.addAll(lines.getOrThrow());
                final DataResult<Integer> currentLine = Codec.intRange(0, procedure.lines.size()).parse(ops, input.get(CURRENT_LINE_KEY));
                if (currentLine.isError()) return currentLine.map(c -> procedure).setPartial(procedure);
                procedure.currentLineNumber = currentLine.getOrThrow();
                return decodeCurrentInstance(ops, input, procedure, procedure.lines.get(procedure.currentLineNumber));
            };

            @Override
            public <T> RecordBuilder<T> encode(final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure, final IScratchContextProvider<?> contextProvider, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
                prefix.add(LINES_KEY, procedure.linesCodec.encodeStart(ops, procedure.lines));
                prefix.add(CURRENT_LINE_KEY, Codec.INT.encodeStart(ops, procedure.currentLineNumber));
                if (procedure.currentLine != null) encodeCurrentInstance(ops, prefix, procedure.currentLine);
                return prefix;
            };

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(LINES_KEY, CURRENT_LINE_KEY, CURRENT_INSTANCE_KEY).map(ops::createString);
            };

            private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> DataResult<ScratchProcedure<ENVIRONMENT, CONTEXT>> decodeCurrentInstance(final DynamicOps<T> ops, final MapLike<T> input, ScratchProcedure<ENVIRONMENT, CONTEXT> procedure, final ScratchProcedure.Line<ENVIRONMENT, ARGUMENTS> line) {
                if (line.block() instanceof IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, ?> instantiableScratchBlock) return decodeCurrentInstanceInternal(ops, input.get(CURRENT_INSTANCE_KEY), procedure, instantiableScratchBlock, line.arguments());
                return DataResult.success(procedure);
            };

            private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>> DataResult<ScratchProcedure<ENVIRONMENT, CONTEXT>> decodeCurrentInstanceInternal(final DynamicOps<T> ops, final T value, final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure, final IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, INSTANCE> block, final ARGUMENTS arguments) {
                if (value == null) return DataResult.success(procedure);
                return block.instanceCodec().parse(ops, arguments, value).map(instance -> {
                    procedure.currentLine = new CurrentLine<>(block, arguments, instance);
                    return procedure;
                });
            };

            private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>> void encodeCurrentInstance(final DynamicOps<T> ops, final RecordBuilder<T> builder, final ScratchProcedure.CurrentLine<ENVIRONMENT, ARGUMENTS, INSTANCE> currentLine) {
                builder.add(CURRENT_INSTANCE_KEY, currentLine.block().instanceCodec().encodeStart(ops, currentLine.arguments(), currentLine.instance()));
            };
            
        };
        
        return mapCodec.codec();
    };

    public static <ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ScratchProcedure<ENVIRONMENT, CONTEXT>> streamCodec() {
        return new ContextualStreamCodec<>() {

            @Override
            public ScratchProcedure<ENVIRONMENT, CONTEXT> decode(final RegistryFriendlyByteBuf buffer, final IScratchContextProvider<?> contextProvider) {
                final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure = new ScratchProcedure<>(contextProvider);
                procedure.lines.addAll(procedure.linesStreamCodec.decode(buffer));
                final int currentLine = ByteBufCodecs.INT.decode(buffer);
                if (currentLine < 0 || currentLine >= procedure.lines.size()) throw new DecoderException("Current line out of range");
                decodeCurrentInstance(buffer, procedure, procedure.lines.get(procedure.currentLineNumber));
                return procedure;
            };

            @Override
            public void encode(final RegistryFriendlyByteBuf buffer, final IScratchContextProvider<?> context, final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure) {
                procedure.linesStreamCodec.encode(buffer, procedure.lines);
                ByteBufCodecs.INT.encode(buffer, procedure.currentLineNumber);
                if (procedure.currentLine != null) encodeCurrentInstance(buffer, procedure.currentLine);
            };

            private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> void decodeCurrentInstance(final RegistryFriendlyByteBuf buffer, final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure, ScratchProcedure.Line<ENVIRONMENT, ARGUMENTS> line) {
                if (line.block() instanceof IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, ?> instantiableScratchBlock) decodeCurrentInstanceInternal(buffer, procedure, instantiableScratchBlock, line.arguments());
            };

            private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>> void decodeCurrentInstanceInternal(final RegistryFriendlyByteBuf buffer, final ScratchProcedure<ENVIRONMENT, CONTEXT> procedure, IInstantiableScratchBlock<? super ENVIRONMENT, ARGUMENTS, INSTANCE> block, ARGUMENTS arguments) {
                procedure.currentLine = new CurrentLine<>(block, arguments, block.instanceStreamCodec().decode(buffer, arguments));
            };

            private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<? super ENVIRONMENT>> void encodeCurrentInstance(final RegistryFriendlyByteBuf buffer, final ScratchProcedure.CurrentLine<ENVIRONMENT, ARGUMENTS, INSTANCE> currentLine) {
                currentLine.block().instanceStreamCodec().encode(buffer, currentLine.arguments(), currentLine.instance());
            };

        };
    };

    private final MapCodec<ScratchProcedure.Line<ENVIRONMENT, ?>> lineMapCodec = new MapCodec<>() {

        private static final String BLOCK_KEY = "block";
        private static final String ARGUMENTS_KEY = "arguments";

        @Override
        @SuppressWarnings("unchecked")
        public <T> DataResult<ScratchProcedure.Line<ENVIRONMENT, ?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            return IScratchBlock.CODEC.parse(ops, environmentType(), input.get(BLOCK_KEY))
                .flatMap(block -> {
                    try {
                        return DataResult.success((IScratchBlock<? super ENVIRONMENT, ?>)block);
                    } catch (ClassCastException e) {
                        return DataResult.error(() -> "Block cannot be cast to this Environment");
                    }
                }).flatMap(block -> decodeInternal(ops, input, block));
        };

        @Override
        public <T> RecordBuilder<T> encode(ScratchProcedure.Line<ENVIRONMENT, ?> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return encodeInternal(input, ops, prefix);
        };

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(BLOCK_KEY, ARGUMENTS_KEY).map(ops::createString);
        };

        private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> DataResult<ScratchProcedure.Line<ENVIRONMENT, ?>> decodeInternal(final DynamicOps<T> ops, final MapLike<T> input, final IScratchBlock<? super ENVIRONMENT, ARGUMENTS> block) {
            return block.getParameters().argumentsCodec().parse(ops, ScratchProcedure.this, input.get(ARGUMENTS_KEY)).map(arguments -> new Line<>(block, arguments));
        };

        private <T, ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> RecordBuilder<T> encodeInternal(ScratchProcedure.Line<ENVIRONMENT, ARGUMENTS> line, DynamicOps<T> ops, RecordBuilder<T> builder) {
            builder.add(BLOCK_KEY, IScratchBlock.CODEC.encodeStart(ops, environmentType(), line.block()));
            builder.add(ARGUMENTS_KEY, line.block().getParameters().argumentsCodec().encodeStart(ops, ScratchProcedure.this, line.arguments()));
            return builder;
        };
        
    };

    private final Codec<List<ScratchProcedure.Line<ENVIRONMENT, ?>>> linesCodec = lineMapCodec.codec().listOf();

    private final StreamCodec<RegistryFriendlyByteBuf, ScratchProcedure.Line<ENVIRONMENT, ?>> lineStreamCodec = new StreamCodec<>() {

        @Override
        @SuppressWarnings("unchecked")
        public Line<ENVIRONMENT, ?> decode(@Nonnull RegistryFriendlyByteBuf buffer) {
            final IScratchBlock<?, ?> block = IScratchBlock.STREAM_CODEC.decode(buffer, environmentType());
            try {
                return decodeInternal(buffer, (IScratchBlock<? super ENVIRONMENT, ?>)block);
            } catch (ClassCastException e) {
                throw new DecoderException(String.format("Block {} has the wrong environment", block.getBlockType()));
            }
        };

        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull ScratchProcedure.Line<ENVIRONMENT, ?> value) {
            encodeInternal(buffer, value);
        };

        private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> ScratchProcedure.Line<ENVIRONMENT, ?> decodeInternal(RegistryFriendlyByteBuf buffer, IScratchBlock<? super ENVIRONMENT, ARGUMENTS> block) {
            return new Line<>(block, block.getParameters().argumentsStreamCodec().decode(buffer, ScratchProcedure.this));
        };

        private <ARGUMENTS extends ScratchArguments<? super ENVIRONMENT, ?>> void encodeInternal(RegistryFriendlyByteBuf buffer, ScratchProcedure.Line<ENVIRONMENT, ARGUMENTS> value) {
            IScratchBlock.STREAM_CODEC.encode(buffer, environmentType(), value.block());
            value.block().getParameters().argumentsStreamCodec().encode(buffer, ScratchProcedure.this, value.arguments());
        };
        
    };

    private final StreamCodec<RegistryFriendlyByteBuf, List<Line<ENVIRONMENT, ?>>> linesStreamCodec = lineStreamCodec.apply(ByteBufCodecs.list());

    
};
