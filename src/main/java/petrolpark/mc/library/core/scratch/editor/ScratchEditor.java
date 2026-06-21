package petrolpark.mc.library.core.scratch.editor;

import java.util.ArrayList;
import java.util.List;

import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.RootScratchContext;

public class ScratchEditor<ENVIRONMENT extends IScratchEnvironment> {

    public final IScratchEnvironment.Type<ENVIRONMENT> environmentType;
    protected final RootScratchContext<ENVIRONMENT> rootContext;

    protected final List<ScratchEditorFragment<ENVIRONMENT>> fragments = new ArrayList<>();

    public ScratchEditor(IScratchEnvironment.Type<ENVIRONMENT> environmentType) {
        this.environmentType = environmentType;
        this.rootContext = new RootScratchContext<>(this.environmentType);
    };
    
};
