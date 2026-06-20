package com.petrolpark.core.scratch.environment;

import java.util.List;

import com.petrolpark.compat.create.RequiresCreate;

@RequiresCreate
public interface ICreateDisplaySourceScratchEnvironment extends IScratchEnvironment {
    
    public List<String> getLines();
};
