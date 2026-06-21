// package petrolpark.mc.library.core.scratch;

// import java.util.function.Function;
// import java.util.stream.Stream;

// import petrolpark.mc.library.core.codec.ContextualCodec;
// import petrolpark.mc.library.core.codec.ContextualStreamCodec;
// import petrolpark.mc.library.core.codec.RecordContextualCodecBuilder;
// import petrolpark.mc.library.core.scratch.argument.IScratchArgument;
// import petrolpark.mc.library.core.scratch.argument.IScratchParameter;
// import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
// import petrolpark.mc.library.core.scratch.procedure.IScratchContextProvider;

// import io.netty.buffer.ByteBuf;
// import net.minecraft.network.RegistryFriendlyByteBuf;

// public interface ScratchPasser<ENVIRONMENT extends IScratchEnvironment> {

//     public static final class None<ENVIRONMENT extends IScratchEnvironment> implements ScratchParameters<ENVIRONMENT, ScratchArguments.None<ENVIRONMENT>> {

//     };

//     public static abstract sealed class More<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>, ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, TYPE, ARGUMENT>> 
//         implements ScratchParameters<ENVIRONMENT, ARGUMENTS>
//         permits Just, And 
//     {
    
//         protected final PARAMETER parameter;

//         protected More(PARAMETER parameter) {
//             this.parameter = parameter;
//         };

//         public PARAMETER get() {
//             return parameter;
//         };

//     };

//     public static final class Just<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>> 
//         extends More<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>>
//     {
//         private final ContextualCodec<IScratchContextProvider<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsCodec = argumentCodec().xmap(ScratchArguments.Just::new, ScratchArguments.Just::argument);
//         private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsStreamCodec = argumentStreamCodec().map(ScratchArguments.Just::new, ScratchArguments.Just::argument);

//         protected Just(PARAMETER parameter) {
//             super(parameter);
//         };

//         @Override
//         public Stream<IScratchParameter<ENVIRONMENT, ?, ?>> stream() {
//             return Stream.of(parameter);
//         };

//         @Override
//         public ContextualCodec<IScratchContextProvider<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsCodec() {
//             return argumentsCodec;
//         };

//         @Override
//         public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>> argumentsStreamCodec() {
//             return argumentsStreamCodec;
//         };

//         public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>> extends ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER> implements ScratchParameters.More.Builder<ENVIRONMENT> {
        
//             protected Builder(PARAMETER parameter) {
//                 super(parameter);
//             };

//             @Override
//             public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>, PREVIOUS_PARAMETER extends IScratchParameter<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT>> ScratchParameters.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, PREVIOUS_PARAMETER, ScratchArguments.Just<ENVIRONMENT, TYPE, ARGUMENT>, ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER>> after(PREVIOUS_PARAMETER parameter) {
//                 return new ScratchParameters.And.Builder<>(parameter, build());
//             };

//             @Override
//             public ScratchParameters.Just<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER> build() {
//                 return new ScratchParameters.Just<>(parameter);
//             };

//         };

//     };

//     public static sealed class And<
//             ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>, NEXT_ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ?, ?>, NEXT extends ScratchParameters.More<ENVIRONMENT, ?, ?, ?, NEXT_ARGUMENTS>
//         > extends More<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> 
//         implements ScratchSignature.And<TYPE, NEXT>
//         permits ScratchParameters.And.Builder
//     {
//         protected final NEXT next;

//         private final ContextualCodec<IScratchContextProvider<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsCodec = RecordContextualCodecBuilder.create(instance -> instance.group(
//             argumentCodec().fieldOf("argument").forGetter(ScratchArguments.And::argument),
//             next().argumentsCodec().fieldOf("next").forGetter(ScratchArguments.And::next)
//         ).apply(instance, ScratchArguments.And::new));

//         private final ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsStreamCodec = ContextualStreamCodec.composite(
//             argumentStreamCodec(), ScratchArguments.And::argument,
//             next().argumentsStreamCodec(), ScratchArguments.And::next,
//             ScratchArguments.And::new
//         );

//         protected And(PARAMETER parameter, NEXT next) {
//             super(parameter);
//             this.next = next;
//         };

//         @Override
//         public Stream<IScratchParameter<ENVIRONMENT, ?, ?>> stream() {
//             return Stream.concat(next.stream(), Stream.<IScratchParameter<ENVIRONMENT, ?, ?>>of(parameter));
//         };

//         @Override
//         public ContextualCodec<IScratchContextProvider<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsCodec() {
//             return argumentsCodec;
//         };

//         @Override
//         public ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchContextProvider<?>, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>> argumentsStreamCodec() {
//             return argumentsStreamCodec;
//         };

//         public NEXT next() {
//             return next;
//         };

//         public static final class Builder<ENVIRONMENT extends IScratchEnvironment, TYPE, ARGUMENT extends IScratchArgument<? super ENVIRONMENT, TYPE>, PARAMETER extends IScratchParameter<ENVIRONMENT, TYPE, ARGUMENT>, NEXT_ARGUMENTS extends ScratchArguments.More<ENVIRONMENT, ?, ?>, NEXT extends ScratchParameters.More<ENVIRONMENT, ?, ?, ?, NEXT_ARGUMENTS>> extends ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, NEXT_ARGUMENTS, NEXT> implements ScratchParameters.More.Builder<ENVIRONMENT> {
        
//             protected Builder(PARAMETER parameter, NEXT next) {
//                 super(parameter, next);
//             };

//             @Override
//             public <PREVIOUS_TYPE, PREVIOUS_ARGUMENT extends IScratchArgument<? super ENVIRONMENT, PREVIOUS_TYPE>, PREVIOUS_PARAMETER extends IScratchParameter<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT>> ScratchParameters.And.Builder<ENVIRONMENT, PREVIOUS_TYPE, PREVIOUS_ARGUMENT, PREVIOUS_PARAMETER, ScratchArguments.And<ENVIRONMENT, TYPE, ARGUMENT, NEXT_ARGUMENTS>, ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, NEXT_ARGUMENTS, NEXT>> after(PREVIOUS_PARAMETER parameter) {
//                 return new ScratchParameters.And.Builder<>(parameter, build());
//             };

//             @Override
//             public ScratchParameters.And<ENVIRONMENT, TYPE, ARGUMENT, PARAMETER, NEXT_ARGUMENTS, NEXT> build() {
//                 return new ScratchParameters.And<>(parameter, next);
//             };

//         };

//     };
// };
