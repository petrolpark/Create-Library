package com.petrolpark.core.scratch.procedure;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.block.IInstantScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IInstantiableScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class ScratchProcedure<ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> implements IScratchContextHolder<CONTEXT> {

    protected final IScratchContextHolder<?> enclosingContextHolder;

    public ScratchProcedure(IScratchContextHolder<?> enclosingContextHolder) {
        this.enclosingContextHolder = enclosingContextHolder;
    };

    protected final List<Line<? super ENVIRONMENT, ?>> lines = new ArrayList<>();
    
    protected int currentLineNumber = 0;
    protected CurrentLine<? super ENVIRONMENT, ?, ?> currentLine = null;

    public boolean run(ENVIRONMENT environment, CONTEXT context) {
        while (currentLine == null || currentLine.run(environment)) {
            currentLine = null;
            if (currentLineNumber < 0 || currentLineNumber >= lines.size()) return true; // Finished
            currentLineNumber++;
            currentLine = lines.get(currentLineNumber).run(environment, context);
        };
        return false;
    };

    public void exit() {
        currentLineNumber = lines.size();
        currentLine = null;
    };

    @Override
    public IScratchContextHolder<?> enclosingContextHolder() {
        return enclosingContextHolder;
    };

    public static <ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> ContextualCodec<IScratchContextHolder<?>, ScratchProcedure<ENVIRONMENT, CONTEXT>> codec() {
        return null; //TODO
    };

    public static <ENVIRONMENT extends IScratchEnvironment, CONTEXT extends IScratchContext<CONTEXT>> ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchProcedure<ENVIRONMENT, CONTEXT>> streamCodec() {
        return null; //TODO
    };

    public static record Line<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>>(IScratchBlock<ENVIRONMENT, ARGUMENTS, ?> block, ARGUMENTS arguments) {

        @Nullable
        public CurrentLine<ENVIRONMENT, ARGUMENTS, ?> run(ENVIRONMENT environment, IScratchContext<?> context) {
            if (block() instanceof final IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, ?, ?> instantiableBlock) return new CurrentLine<>(instantiableBlock, arguments(), environment, context);
            else if (block() instanceof final IInstantScratchBlock<ENVIRONMENT, ARGUMENTS, ?> instantBlock) instantBlock.run(environment, context, arguments());
            return null;
        };
    };

    public static record CurrentLine<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>, INSTANCE extends IScratchBlockInstance<ENVIRONMENT>>(IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE, ?> block, ARGUMENTS arguments, INSTANCE instance) {

        public CurrentLine(IInstantiableScratchBlock<ENVIRONMENT, ARGUMENTS, INSTANCE, ?> block, ARGUMENTS arguments, ENVIRONMENT environment, IScratchContext<?> context) {
            this(block, arguments, block.run(environment, context, arguments));
        };

        public boolean run(ENVIRONMENT environment) {
            return instance().run(environment);  
        };
    };
};
