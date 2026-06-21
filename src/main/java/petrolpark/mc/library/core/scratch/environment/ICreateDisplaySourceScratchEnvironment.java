package petrolpark.mc.library.core.scratch.environment;

import java.util.List;

import petrolpark.mc.library.compat.create.RequiresCreate;

@RequiresCreate
public interface ICreateDisplaySourceScratchEnvironment extends IScratchEnvironment {
    
    public List<String> getLines();
};
