package petrolpark.mc.library.registry.scratch;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.block.FlexibleEnvironmentScratchBlockType;
import petrolpark.mc.library.core.scratch.symbol.block.GenericInstantBlock;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlock;
import petrolpark.mc.library.core.scratch.symbol.block.control.IfBlock;
import petrolpark.mc.library.core.scratch.symbol.block.control.RepeatBlock;
import petrolpark.mc.library.core.scratch.symbol.block.control.WaitBlock;
import petrolpark.mc.library.core.scratch.symbol.block.variable.AssignBlock;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchBlockTypes {
    
    // Control

    public static final RegistryEntry<IScratchBlock.Type<?>, FlexibleEnvironmentScratchBlockType<IScratchEnvironment, IfBlock<?>>> IF = REGISTRATE.flexibleEnvironmentScratchBlockType("if", IScratchEnvironment.class, IfBlock::create);
    public static final RegistryEntry<IScratchBlock.Type<?>, FlexibleEnvironmentScratchBlockType<IScratchEnvironment, RepeatBlock<?>>> REPEAT = REGISTRATE.flexibleEnvironmentScratchBlockType("repeat", IScratchEnvironment.class, RepeatBlock::create);
    public static final RegistryEntry<IScratchBlock.Type<?>, WaitBlock> WAIT = REGISTRATE.scratchBlockType("wait", WaitBlock::new);

    // Variable Manipulation

    public static final RegistryEntry<IScratchBlock.Type<?>, GenericInstantBlock.Type<AssignBlock<?, ?>>> ASSIGN = REGISTRATE.genericScratchBlockType("assign", AssignBlock::create);

    public static final void register() {};
};
