package com.petrolpark.core.scratch.classes;

public interface IParseableScratchClass<TYPE> extends IScratchClass<TYPE> {
    
    public TYPE parse(String string);
};
