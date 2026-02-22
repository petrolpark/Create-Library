package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.block.FlexibleEnvironmentScratchBlockType;
import com.petrolpark.core.scratch.symbol.block.GenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.block.control.IfBlock;
import com.petrolpark.core.scratch.symbol.block.control.RepeatBlock;
import com.petrolpark.core.scratch.symbol.block.control.WaitBlock;
import com.petrolpark.core.scratch.symbol.block.variable.AssignBlock;
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
