package petrolpark.mc.library.core.scratch.editor;

import petrolpark.mc.library.core.scratch.ScratchArguments;
import petrolpark.mc.library.core.scratch.editor.ScratchEditorFragment.LooseSymbolFragment;
import petrolpark.mc.library.core.scratch.editor.ScratchEditorFragment.ProcedureFragment;
import petrolpark.mc.library.core.scratch.editor.ScratchEditorFragment.TriggerFragment;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.procedure.RootScratchContext;
import petrolpark.mc.library.core.scratch.procedure.ScratchProcedure;
import petrolpark.mc.library.core.scratch.symbol.IScratchSymbol;

public sealed abstract class ScratchEditorFragment<ENVIRONMENT extends IScratchEnvironment> permits LooseSymbolFragment, ProcedureFragment, TriggerFragment {

    public int x = 0;
    public int y = 0;
  
    public static final class LooseSymbolFragment<ENVIRONMENT extends IScratchEnvironment, ARGUMENTS extends ScratchArguments<ENVIRONMENT, ?>> extends ScratchEditorFragment<ENVIRONMENT> {

        protected final IScratchSymbol<? extends ENVIRONMENT, ARGUMENTS, ?> symbol;
        protected final ARGUMENTS arguments;

        public LooseSymbolFragment(IScratchSymbol<? extends ENVIRONMENT, ARGUMENTS, ?> symbol, ARGUMENTS arguments) {
            this.symbol = symbol;
            this.arguments = arguments;
        };
    };

    public static final class ProcedureFragment<ENVIRONMENT extends IScratchEnvironment> extends ScratchEditorFragment<ENVIRONMENT> {

        protected final ScratchProcedure<ENVIRONMENT, RootScratchContext<ENVIRONMENT>> procedure;

        public ProcedureFragment(ScratchProcedure<ENVIRONMENT, RootScratchContext<ENVIRONMENT>> procedure) {
            this.procedure = procedure;
        };
    };

    public static final class TriggerFragment<ENVIRONMENT extends IScratchEnvironment> extends ScratchEditorFragment<ENVIRONMENT> {

    };
};
