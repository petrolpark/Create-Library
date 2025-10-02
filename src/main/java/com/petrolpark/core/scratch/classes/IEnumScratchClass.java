package com.petrolpark.core.scratch.classes;

import java.util.List;

import com.petrolpark.core.scratch.IScratchClass;
import com.petrolpark.core.scratch.argument.DropdownArgument;
import com.petrolpark.core.scratch.argument.IScratchArgument;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;

public interface IEnumScratchClass<TYPE, DEFAULT_ARGUMENT extends IScratchArgument<IScratchEnvironment, TYPE>> extends IScratchClass<TYPE, DEFAULT_ARGUMENT> {
    
    public List<DropdownArgument.Named<TYPE>> values();
};
