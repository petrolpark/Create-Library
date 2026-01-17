package com.petrolpark.core.scratch.classes;

import com.petrolpark.core.scratch.IScratchClass;

public interface IParseableScratchClass<TYPE> extends IScratchClass<TYPE> {
    
    public TYPE parse(String string);
};
