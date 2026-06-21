package petrolpark.mc.library.core.scratch.procedure;

public interface IScratchContextHolder {
    
    public <CONTEXT extends IScratchContext<CONTEXT>> void populateContext(IScratchContextProvider<CONTEXT> contextProvider, CONTEXT context);

    public static IScratchContextHolder cast(Object object) {
        return object instanceof IScratchContextHolder contextHolder ? contextHolder : null;
    };
};
