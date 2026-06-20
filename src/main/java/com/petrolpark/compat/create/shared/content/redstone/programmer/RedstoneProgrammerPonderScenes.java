package com.petrolpark.compat.create.shared.content.redstone.programmer;

import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class RedstoneProgrammerPonderScenes {
  
    public static void redstoneProgrammer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("redstone_programmer", "Using the Redstone Programmer");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(10);
        for (int i = 0; i < 9; i++) {
            scene.world().showSection(util.select().position(1 + i % 3, 1 + i / 3, 3), Direction.DOWN);
            scene.idle(5);
        };

        for (int i = 0; i < 3; i++) {
            BlockPos pos = util.grid().at(1 + i, 3, 3);
            scene.overlay().showControls(util.vector().blockSurface(pos, Direction.SOUTH)
                .add(0, 0, -3 / 16f), Pointing.DOWN, 10)
                .rightClick()
                .withItem(AllItems.WRENCH.asStack());
            scene.world().modifyBlock(pos, s -> s.cycle(RedstoneLinkBlock.RECEIVER), true);
            scene.idle(10);
        };

        scene.idle(20);
        Vec3 linkVec = util.vector().blockSurface(util.grid().at(2, 3, 3), Direction.SOUTH).add(0, 0, -3 / 16f);
        scene.overlay().showControls(linkVec, Pointing.DOWN, 80).rightClick().withItem(SharedCreateBlocks.REDSTONE_PROGRAMMER.asStack());
        scene.overlay().showText(80)
            .text("Click on a Redstone Link with a Redstone Programmer to add that channel to the Programmer.")
            .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.UP))
            .attachKeyFrame();
        scene.idle(100);

        scene.overlay().showText(80)
            .text("With JEI installed, you can also drag in items manually in the GUI.");
        scene.idle(100);

        Vec3 placementPos = util.vector().blockSurface(util.grid().at(2, 0, 1), Direction.UP);
        scene.overlay().showText(100)
            .text("Shift-click to place the Programmer in the world.")
            .pointAt(placementPos)
            .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(placementPos, Pointing.DOWN, 40).rightClick().whileSneaking().withItem(SharedCreateBlocks.REDSTONE_PROGRAMMER.asStack());
        scene.idle(40);
        scene.world().showSection(util.select().position(2, 1, 1), Direction.DOWN);
        scene.idle(80);

        scene.overlay().showText(100)
            .text("While placed or in a Player's inventory, Programmers can broadcast sequences of wireless Redstone signals.")
            .attachKeyFrame();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                Selection selection = util.select().fromTo(1 + j, 1, 3, 1 + j, 3, 3);
                scene.world().toggleRedstonePower(selection);
                scene.idle(10);
            };
        };
        scene.markAsFinished();
    };
};
