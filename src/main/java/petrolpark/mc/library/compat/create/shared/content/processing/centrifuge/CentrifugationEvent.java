package petrolpark.mc.library.compat.create.shared.content.processing.centrifuge;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.bus.api.Event;

public class CentrifugationEvent extends Event {
    
    protected final CentrifugeBlockEntity centrifuge;
    protected final List<ICentrifugationRecipe> recipes = new ArrayList<>();

    public CentrifugationEvent(CentrifugeBlockEntity centrifuge) {
        this.centrifuge = centrifuge;
    };

    public CentrifugeBlockEntity getCentrifuge() {
        return centrifuge;
    };

    public void addRecipe(ICentrifugationRecipe recipe) {
        recipes.add(recipe);
    };
};
