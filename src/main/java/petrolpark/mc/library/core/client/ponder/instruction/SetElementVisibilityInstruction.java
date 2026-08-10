package petrolpark.mc.library.core.client.ponder.instruction;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;

public class SetElementVisibilityInstruction extends PonderInstruction {

    protected final ElementLink<? extends PonderElement> link;
    protected final boolean visibility;

    public SetElementVisibilityInstruction(ElementLink<?> link, boolean visibility) {
        this.link = link;
        this.visibility = visibility;
    };

    @Override
    public boolean isComplete() {
        return true;
    };

    @Override
    public void tick(PonderScene scene) {
        final PonderElement element = scene.resolve(link);
        if (element != null) element.setVisible(visibility);
    };
    
};
