package petrolpark.mc.library.core.scratch.symbol.block.control;

import static petrolpark.mc.library.core.scratch.argument.ContextArgument.contextParameter;
import static petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.integerParameter;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.argument.ContextArgument;
import petrolpark.mc.library.core.scratch.argument.ContextArgument.ContextParameter;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument;
import petrolpark.mc.library.core.scratch.argument.ExpressionOrLiteralArgument.ExpressionOrLiteralParameter;
import petrolpark.mc.library.core.scratch.argument.NestedProcedureArgument;
import petrolpark.mc.library.core.scratch.classes.IntegerScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlock;
import petrolpark.mc.library.core.scratch.symbol.block.UnaryInstantBlockType;
import petrolpark.mc.library.core.scratch.symbol.block.UnaryNestedProcedureBlock;
import petrolpark.mc.library.core.scratch.symbol.block.instance.NestedProcedureBlockInstance;
import petrolpark.mc.library.core.scratch.symbol.expression.UnaryExpressionType;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchBlockTypes;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.util.codec.ContextualCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;
import petrolpark.mc.library.util.codec.RecordContextualCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public class RepeatBlock<ENVIRONMENT extends IScratchEnvironment> extends UnaryNestedProcedureBlock<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>, ExpressionOrLiteralParameter<ENVIRONMENT, Long>, RepeatBlock.Instance<ENVIRONMENT>, RepeatBlock<ENVIRONMENT>> {

    private final ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceCodec = RecordContextualCodecBuilder.create(instance -> instance.group(
        ContextualCodec.<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock. Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, Long>of(Codec.LONG).fieldOf("repeats").forGetter(RepeatBlock.Instance::remainingRepeats),
        instance.context()
    ).apply(instance, RepeatBlock.Instance::new));

    private final ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceStreamCodec = ContextualStreamCodec.<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, Long>of(ByteBufCodecs.VAR_LONG).map(RepeatBlock.Instance::new, RepeatBlock.Instance::remainingRepeats);

    public static final <ENVIRONMENT extends IScratchEnvironment> RepeatBlock<ENVIRONMENT> create(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        return new RepeatBlock<>(environmentType);
    };

    protected RepeatBlock(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        super(integerParameter("repeats"));
    };

    @Override
    public RepeatBlock.Instance<ENVIRONMENT> run(ENVIRONMENT environment, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> procedureArgument, Long argument) {
        return new RepeatBlock.Instance<>(procedureArgument, argument);
    };

    @Override
    public ContextualCodec<ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceCodec() {
        return instanceCodec;
    };

    @Override
    public ContextualStreamCodec<ByteBuf, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>>, RepeatBlock.Instance<ENVIRONMENT>> instanceStreamCodec() {
        return instanceStreamCodec;
    };

    @Override
    public IScratchBlock.Type<RepeatBlock<?>> getBlockType() {
        return PetrolparkScratchBlockTypes.REPEAT.get();
    };

    public static class Instance<ENVIRONMENT extends IScratchEnvironment> extends NestedProcedureBlockInstance<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> {

        protected long remainingRepeats;

        protected Instance(long repeats, ScratchArguments.And<ENVIRONMENT, ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, NestedProcedureArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ScratchArguments.Just<ENVIRONMENT, Long, ExpressionOrLiteralArgument<ENVIRONMENT, Long>>> arguments) {
            this(arguments.argument().procedure(), repeats);  
        };

        protected Instance(ScratchProcedure<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>> procedure, long repeats) {
            super(procedure);
            this.remainingRepeats = repeats;
        };

        @Override
        protected RepeatBlock.Instance<ENVIRONMENT> self() {
            return this;
        };

        public long remainingRepeats() {
            return remainingRepeats;  
        };

        @Override
        public boolean tick(ENVIRONMENT environment) {
            while (remainingRepeats > 0) {
                if (procedure().tick(environment)) {
                    remainingRepeats--;
                } else {
                    return false;  
                };
            };
            return true;
        };

    };

    public static class RemainingRepeatsExpression<
        ENVIRONMENT extends IScratchEnvironment
    > extends UnaryExpressionType<
        ENVIRONMENT, 
        Long,
        RepeatBlock.Instance<ENVIRONMENT>, ContextArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ContextParameter<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>,
        RemainingRepeatsExpression<ENVIRONMENT>
    > {

        protected RemainingRepeatsExpression() {
            super(contextParameter("instance"));
        };

        @Override
        public IntegerScratchClass getReturnClass() {
            return PetrolparkScratchClasses.INTEGER.get();
        };

        @Override
        public Long evaluate(ENVIRONMENT environment, RepeatBlock.Instance<ENVIRONMENT> blockInstance) {
            return blockInstance.remainingRepeats();
        };

        @Override
        protected RemainingRepeatsExpression<ENVIRONMENT> self() {
            return this;
        };

    };

    public static class BreakBlock<
        ENVIRONMENT extends IScratchEnvironment
    > extends UnaryInstantBlockType<
        ENVIRONMENT,
        RepeatBlock.Instance<ENVIRONMENT>, ContextArgument<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>, ContextParameter<ENVIRONMENT, RepeatBlock.Instance<ENVIRONMENT>>,
        BreakBlock<ENVIRONMENT>
    > {

        protected BreakBlock() {
            super(contextParameter("instance"));
        };

        @Override
        public void run(ENVIRONMENT environment, RepeatBlock.Instance<ENVIRONMENT> argument) {
            argument.remainingRepeats = 0;
            argument.procedure().exit();
        };

        @Override
        protected BreakBlock<ENVIRONMENT> self() {
            return this;
        };

    };
    
};
