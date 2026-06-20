package com.petrolpark.compat.create.core.world.block.crushingWheel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class CrushingWheelScenes {
    
    public static final void filtering(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn); 
        scene.title("crushing_wheel_filtering", "This text is defined in a language file");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.9f); 

        final BlockPos middle = util.grid().at(2, 2, 2);
        final Vec3 top = util.vector().topOf(middle);
        final Vec3 side = util.vector().blockSurface(middle, Direction.NORTH);

        scene.world().showSection(util.select().layer(0), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 4, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 2, 3, 3, 2, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 2, 2, 3, 2, 2).substract(util.select().position(middle)), Direction.DOWN);
        scene.idle(5);

        scene.overlay().showText(60)
            .pointAt(top)
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(30);
        scene.overlay().showControls(side, Pointing.RIGHT, 30)
            .rightClick()
            .withItem(AllBlocks.BRASS_CASING.asStack());
        scene.idle(5);
        final ElementLink<WorldSectionElement> controller = scene.world().showIndependentSectionImmediately(util.select().position(middle));
        scene.idle(45);

        scene.overlay().showText(100)
            .pointAt(top)
            .colored(PonderPalette.GREEN)
            .text("This text is defined in a language file");
        scene.idle(120);

        final Vec3 filterPos = util.vector().of(2.5f, 2.5f, 2f);
        scene.overlay().showFilterSlotInput(filterPos, Direction.NORTH, 80);
		scene.overlay().showText(80)
			.pointAt(filterPos)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(100);

        scene.overlay().showText(80)
            .pointAt(top)
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(30);
        scene.overlay().showControls(side, Pointing.RIGHT, 50)
            .rightClick()
            .withItem(AllItems.WRENCH.asStack());
        scene.idle(5);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.UP, controller));
        scene.idle(45);
    };
};
