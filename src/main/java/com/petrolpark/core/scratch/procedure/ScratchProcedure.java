package com.petrolpark.core.scratch.procedure;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.core.codec.ContextualCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.scratch.ScratchArguments;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlockInstance;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class ScratchProcedure<ENVIRONMENT extends IScratchEnvironment> {

    protected final List<Line<? super ENVIRONMENT, ?>> lines = new ArrayList<>();
    
    protected int currentLine = 0;
    protected IScratchBlockInstance<? super ENVIRONMENT> currentInstance = null;

    public boolean run(ENVIRONMENT environment, IScratchContext<?> context) {
        while (currentInstance == null || currentInstance.run(environment)) {
            currentInstance = null;
            if (currentLine < 0 || currentLine >= lines.size()) return true; // Finished
            currentLine++;
            currentInstance = lines.get(currentLine).run(environment, context);
        };
        return false;
    };

    public void exit() {
        currentLine = lines.size();
        currentInstance = null;
    };

    public static <ENVIRONMENT extends IScratchEnvironment> ContextualCodec<IScratchContextHolder<?>, ScratchProcedure<ENVIRONMENT>> codec() {
        return null; //TODO
    };

    public static <ENVIRONMENT extends IScratchEnvironment> ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextHolder<?>, ScratchProcedure<ENVIRONMENT>> streamCodec() {
        return null; //TODO
    };

    public static record Line<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>>(IScratchBlock<ENVIRONMENT, ARGUMENTS, ?> block, ARGUMENTS arguments) {

        public IScratchBlockInstance<ENVIRONMENT> run(ENVIRONMENT environment, IScratchContext<?> context) {
            return block.run(environment, context, arguments);
        };
    };
};
