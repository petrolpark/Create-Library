package petrolpark.mc.library.compat.create.core.world.dough.client;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

public class DoughScenes {
    
    public static final void rollingPin(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
        scene.title("dough.rolling_pin", "This text is defined in a language file");
    };

    public static final void mechanicalPress(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
        scene.title("dough.mechanical_press", "This text is defined in a language file");
    };
};
