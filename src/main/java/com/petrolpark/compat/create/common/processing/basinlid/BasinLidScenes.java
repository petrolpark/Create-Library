package com.petrolpark.compat.create.common.processing.basinlid;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class BasinLidScenes {
    
    public static void basinLid(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("basin_lid", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);

        final BlockPos underneath = util.grid().at(1, 1, 2);
        final BlockPos basin = util.grid().at(1, 2, 2);
		final BlockPos lid = util.grid().at(1, 3, 2);
        final Vec3 basinSide = util.vector().blockSurface(basin, Direction.WEST);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(underneath), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(basin), Direction.DOWN);
		scene.idle(5);
        scene.world().showSection(util.select().position(lid), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(3, 1, 1, 1, 1, 1), Direction.SOUTH);
		scene.world().showSection(util.select().fromTo(3, 1, 5, 3, 1, 2), Direction.SOUTH);
        scene.idle(5);

        scene.overlay().showText(105)
			.pointAt(basinSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file.");
		scene.idle(40);

        final ItemStack eye = new ItemStack(Items.SPIDER_EYE);
		final ItemStack sugar = new ItemStack(Items.SUGAR);
		final ItemStack mushrooom = new ItemStack(Items.BROWN_MUSHROOM);
        final ItemStack fermemtedEye = new ItemStack(Items.FERMENTED_SPIDER_EYE);
        
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.LEFT, 15).withItem(eye);
        scene.idle(25);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.LEFT, 15).withItem(sugar);
        scene.idle(25);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.LEFT, 15).withItem(mushrooom);
        scene.idle(30);
        scene.world().modifyBlockEntity(lid, BasinLidBlockEntity.class, be -> {
            be.bubbling = true;
            be.processingTicksRemaining = 100;
        });
        scene.idle(80);
        scene.world().createItemOnBelt(util.grid().at(1, 1, 1), Direction.UP, fermemtedEye);
        scene.idle(80);

        scene.rotateCameraY(-30);
		scene.idle(10);
		scene.world().hideSection(util.select().position(underneath), Direction.EAST);
        scene.idle(10);
        final ElementLink<WorldSectionElement> burner = scene.world().showIndependentSection(util.select().position(0, 1, 2), Direction.EAST);
        scene.world().moveSection(burner, util.vector().of(1d, 0d, 0d), 0);
		scene.idle(10);
		scene.overlay().showText(80)
			.pointAt(basinSide.subtract(0, 1, 0))
			.placeNearTarget()
			.text("This text is defined in a language file");
		scene.idle(40);
		scene.rotateCameraY(30);

		scene.idle(60);
		Vec3 filterPos = util.vector().of(1, 2.75f, 2.5f);
		scene.overlay().showFilterSlotInput(filterPos, Direction.WEST, 100);
		scene.overlay().showText(100)
			.pointAt(filterPos)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(80);

        scene.markAsFinished();
    };
};
