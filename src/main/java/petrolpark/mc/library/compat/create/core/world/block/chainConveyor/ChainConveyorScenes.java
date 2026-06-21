package petrolpark.mc.library.compat.create.core.world.block.chainConveyor;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity.Phase;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.FrogAndConveyorScenes;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChainConveyorScenes {
    
    public static void mechanicalArmChainConveyor(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("mechanical_arm_chain_conveyor", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(.75f);
		scene.setSceneOffsetY(-1);

		final BlockPos conv1 = util.grid().at(1, 4, 7);
		final BlockPos conv2 = util.grid().at(7, 4, 7);
		final BlockPos conv3 = util.grid().at(7, 4, 1);
        final BlockPos arm1 = util.grid().at(5, 2, 3);
        final BlockPos depot = util.grid().at(5, 1, 1);
		final BlockPos arm2 = util.grid().at(6, 1, 6);
		final BlockPos arm3 = util.grid().at(3, 2, 5);

        final Selection arm1S = util.select().position(arm1);
		final Selection arm2S = util.select().position(arm2);
		final Selection conv1S = util.select().position(conv1);
		final Selection conv2S = util.select().position(conv2);
		final Selection conv3S = util.select().position(conv3);
		final Selection largeCog1 = util.select().position(2, 1, 8);
		final Selection shaftPole = util.select().fromTo(1, 1, 7, 1, 3, 7);
		final Selection pole2 = util.select().fromTo(7, 1, 7, 7, 3, 7);
		final Selection pole3 = util.select().fromTo(7, 1, 1, 7, 3, 1);
		final Selection largeCog2 = util.select().position(9, 0, 1);
		final Selection largeCog3 = util.select().position(5, 0, 9);
		final Selection smallCogs = util.select().fromTo(5, 1, 7, 6, 1, 8);

        final Vec3 target1 = util.vector().of(6.78d, 4.37d, 3.5d);

		scene.world().showSection(util.select().layer(0).substract(largeCog2).substract(largeCog3), Direction.UP);
		scene.idle(10);
		scene.world().showSection(largeCog1, Direction.SOUTH);
		scene.idle(2);
		scene.world().showSection(shaftPole, Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(pole2, Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(pole3, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(conv1S, Direction.DOWN);
		scene.world().showSection(conv2S, Direction.DOWN);
		scene.world().showSection(conv3S, Direction.DOWN);
		scene.idle(5);
        scene.world().showSection(util.select().fromTo(6, 1, 2, 6, 2, 2), Direction.DOWN);
        scene.idle(2);
        scene.world().showSection(util.select().position(5, 1, 3), Direction.DOWN);

        scene.idle(25);
        
        final ItemStack armItem = AllBlocks.MECHANICAL_ARM.asStack();
		scene.overlay()
			.showControls(target1, Pointing.UP, 50)
			.rightClick()
			.withItem(armItem);
		scene.idle(5);

        final AABB bb1 = new AABB(target1, target1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, conv1, bb1, 10);
		scene.idle(1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, conv1, bb1.inflate(0.125d, 0.125d, 0.125d), 50);
		scene.idle(26);

		scene.overlay()
			.showText(80)
			.text("This text is defined in a language file")
			.attachKeyFrame()
			.placeNearTarget()
			.pointAt(target1);
		scene.idle(40);
		scene.world().showSection(arm1S, Direction.DOWN);
        scene.idle(40);
		final ItemStack packageItem = new ItemStack(BuiltInRegistries.ITEM.get(Create.asResource("cardboard_package_10x8")));
        scene.world().showSection(util.select().position(depot), Direction.DOWN);
        scene.idle(10);
		scene.world().createItemOnBeltLike(depot, Direction.UP, packageItem);
		scene.overlay().showControls(util.vector().blockSurface(depot, Direction.WEST), Pointing.LEFT, 20)
			.withItem(packageItem);
		scene.idle(10);

        scene.addKeyframe();
        scene.world().instructArm(arm1, Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(35);
        scene.world().removeItemsFromBelt(depot);
        scene.world().instructArm(arm1, Phase.SEARCH_OUTPUTS, packageItem, -1);
        scene.idle(20);
        scene.world().instructArm(arm1, Phase.MOVE_TO_OUTPUT, ItemStack.EMPTY, 0);
        scene.idle(30);
		scene.world().instructArm(arm1, Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.world().modifyBlockEntity(conv3, ChainConveyorBlockEntity.class, be -> {
            be.addTravellingPackage(new ChainConveyorPackage(0.97711223f, packageItem), util.grid().at(0, 0, 6));
        });
		scene.idle(50);

		scene.rotateCameraY(-30f);
		scene.world().showSection(largeCog3, Direction.NORTH);
		scene.idle(5);
		scene.world().showSection(smallCogs, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(arm2S, Direction.SOUTH);
		scene.idle(10);
		scene.overlay().showText(70)
			.attachKeyFrame()
			.pointAt(util.vector().blockSurface(arm2, Direction.WEST))
			.text("This text is defined in a language file");
		scene.idle(15);
		scene.world().instructArm(arm2, Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
		scene.idle(40);
		scene.world().instructArm(arm2, Phase.SEARCH_OUTPUTS, ItemStack.EMPTY, -1);
		scene.idle(30);

		scene.rotateCameraY(60f);
		scene.world().hideSection(arm2S, Direction.UP);
		scene.world().hideSection(smallCogs, Direction.UP);
		scene.world().hideSection(largeCog3, Direction.UP);
		scene.idle(10);
		scene.world().showSection(util.select().position(3, 1, 7), Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(util.select().fromTo(3, 1, 6, 3, 2, 6), Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(util.select().position(3, 1, 5), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(arm3), Direction.DOWN);
		scene.idle(10);

		scene.overlay().showText(90)
			.attachKeyFrame()
			.pointAt(util.vector().blockSurface(arm3, Direction.WEST))
			.text("This text is defined in a language file");
		scene.idle(47);
		scene.world().instructArm(arm3, Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
		scene.idle(15);
		scene.world().modifyBlockEntity(conv2, ChainConveyorBlockEntity.class, be -> FrogAndConveyorScenes.boxTransfer(conv1, conv2, be));
		scene.idle(40);

		scene.rotateCameraY(-30f);
		scene.idle(20);
		scene.world().createItemOnBeltLike(depot, Direction.UP, packageItem);
		scene.overlay().showControls(util.vector().blockSurface(depot, Direction.WEST), Pointing.LEFT, 20)
			.withItem(packageItem);
		scene.idle(10);
		scene.world().instructArm(arm1, Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(35);
        scene.world().removeItemsFromBelt(depot);
        scene.world().instructArm(arm1, Phase.SEARCH_OUTPUTS, packageItem, -1);
        scene.idle(20);
        scene.world().instructArm(arm1, Phase.MOVE_TO_OUTPUT, ItemStack.EMPTY, 0);
        scene.idle(30);
		scene.world().instructArm(arm1, Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
		scene.world().modifyBlockEntity(conv3, ChainConveyorBlockEntity.class, be -> {
            be.addTravellingPackage(new ChainConveyorPackage(0.97711223f, packageItem), util.grid().at(0, 0, 6));
        });
		scene.overlay().showText(90)
			.pointAt(util.vector().topOf(conv2))
			.colored(PonderPalette.RED)
			.text("This text is defined in a language file");
		scene.idle(87);
		scene.world().modifyBlockEntity(conv2, ChainConveyorBlockEntity.class, be -> FrogAndConveyorScenes.boxTransfer(conv1, conv2, be));
    };

    public static void drying(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("drying_chain_conveyor", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(.75f);
		scene.setSceneOffsetY(-1);

		final BlockPos conv1 = util.grid().at(7, 4, 1);
		final BlockPos conv2 = util.grid().at(1, 4, 7);
		final Vec3 selectedPoint = util.vector().of(5.579621d, 4.375000d, 2.406506d);
		final BlockPos arm = util.grid().at(3, 1, 3);

		final ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
		final ItemStack drySponge = new ItemStack(Items.SPONGE);

        scene.showBasePlate();
		scene.idle(2);
		scene.world().showSection(util.select().position(2, 0, 9), Direction.NORTH);
		scene.idle(2);
		scene.world().showSection(util.select().position(2, 1, 8), Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(util.select().fromTo(1, 1, 7, 1, 3, 7), Direction.DOWN);
		scene.idle(2);
		scene.world().showSection(util.select().fromTo(7, 1, 1, 7, 3, 1), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(conv1), Direction.DOWN);
		scene.world().showSection(util.select().position(conv2), Direction.DOWN);
		scene.idle(10);

		scene.overlay().showText(60)
			.pointAt(selectedPoint)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		final AABB bb1 = new AABB(selectedPoint, selectedPoint);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.WHITE, "point", bb1, 10);
		scene.idle(1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.WHITE, "point", bb1.inflate(0.1d, 0.1d, 0.1d), 59);
		scene.idle(20);
		scene.overlay().showControls(selectedPoint, Pointing.DOWN, 39)
			.rightClick()
			.withItem(wetSponge);
		scene.idle(5);
		scene.world().modifyBlockEntity(conv2, ChainConveyorBlockEntity.class, be -> be.addTravellingPackage(new ChainConveyorPackage(5.462394f, wetSponge), conv1.subtract(conv2)));
		scene.idle(50);
		scene.world().modifyBlockEntity(conv1, ChainConveyorBlockEntity.class, be -> {
			be.getLoopingPackages().remove(0);
			be.addTravellingPackage(new ChainConveyorPackage(0f, new ItemStack(Items.SPONGE)), conv2.subtract(conv1));
		});
		scene.idle(10);
		scene.overlay().showControls(util.vector().of(5d, 4.25d, 5d), Pointing.DOWN, 30)
			.withItem(drySponge);
		scene.idle(40);
		for (int z = 7; z >= 3; z--) {
			scene.world().showSection(util.select().position(3, 1, z), Direction.DOWN);
			scene.idle(2);
		};
		scene.idle(5);
		scene.overlay().showText(60)
			.pointAt(util.vector().blockSurface(arm, Direction.WEST))
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(42);
		scene.world().modifyBlockEntity(conv2, ChainConveyorBlockEntity.class, be -> FrogAndConveyorScenes.boxTransfer(conv1, conv2, be));
		scene.world().instructArm(arm, Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
    };
};
