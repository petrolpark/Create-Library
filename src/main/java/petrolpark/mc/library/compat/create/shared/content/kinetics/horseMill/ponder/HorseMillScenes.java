package petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill.ponder;

import petrolpark.mc.library.core.client.ponder.instruction.OutlineAABBInstruction;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HorseMillScenes {
    
    @SuppressWarnings("null")
    public static final void horseMill(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
        scene.title("horse_mill", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 9);
		scene.scaleSceneView(.75f);

        scene.showBasePlate();

        final ElementLink<EntityElement> cow = scene.world().createEntity(level -> {
            final Cow entity = EntityType.COW.create(level);
            final Vec3 p = util.vector().topOf(util.grid().at(2, 0, 1));
			entity.setPos(p.x, p.y, p.z);
			entity.xo = p.x;
			entity.yo = p.y;
			entity.zo = p.z;
			entity.yRotO = 235;
			entity.setYRot(235f);
            entity.yBodyRot = entity.yBodyRotO = entity.yHeadRot = entity.yHeadRotO = 235f;
            return entity;
        });

        scene.idle(10);
        final ElementLink<WorldSectionElement> wrongHarness = scene.world().showIndependentSection(util.select().position(5, 1, 2), Direction.DOWN);
        scene.world().rotateSection(wrongHarness, 0, 90, 0, 0);
        scene.world().moveSection(wrongHarness, util.vector().of(1d, 0d, 1d), 0);
        final ElementLink<WorldSectionElement> emptyHarness = scene.world().showIndependentSection(util.select().position(1, 1, 3), Direction.DOWN);
        scene.world().moveSection(emptyHarness, util.vector().of(1d, 0d, 0d), 0);

        final ElementLink<WorldSectionElement> mill = scene.world().showIndependentSection(util.select().fromTo(2, 1, 2, 6, 2, 6)
            .substract(util.select().position(2, 1, 3))
            .substract(util.select().position(5, 1, 2)),
            Direction.DOWN
        );

        final ItemStack lead = new ItemStack(Items.LEAD);

        scene.idle(10);
        scene.overlay().showText(70)
            .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 3), Direction.SOUTH))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.overlay().showControls(util.vector().of(2.5, 2.25, 1.5), Pointing.DOWN, 20)
            .withItem(lead);
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(2, 1, 3), Pointing.DOWN, 20)
            .withItem(lead);
        scene.idle(10);
        scene.world().modifyEntity(cow, Entity::discard);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, emptyHarness)); // Hide immediately
        scene.addInstruction(new DisplayWorldSectionInstruction(0, Direction.NORTH, util.select().position(2, 1, 3), () -> scene.getScene().resolve(mill))); // Show immediately
        scene.idle(40);

        scene.overlay().showOutline(PonderPalette.GREEN, "glue", util.select().fromTo(2, 1, 2, 6, 2, 6), 90);
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 2, 2), Direction.NORTH), Pointing.RIGHT, 80)
            .withItem(AllItems.SUPER_GLUE.asStack());
        scene.idle(20);
        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 4), Direction.EAST))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(40);

        final BlockPos bearing = util.grid().at(4, 3, 4);
        scene.world().showSection(util.select().position(bearing), Direction.DOWN);
        scene.idle(30);

        scene.rotateCameraY(45);
        scene.idle(10);
        scene.overlay().showText(80)
            .pointAt(util.vector().of(6.5, 2.5, 3))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.addInstruction(new OutlineAABBInstruction(PonderPalette.RED, "naughty cow", new AABB(6, 1, 2, 7, 2.5, 4), 30));
        scene.idle(30);
        scene.world().moveSection(wrongHarness, util.vector().of(-1d, 0, -1d), 20);
        scene.world().rotateSection(wrongHarness, 0, -90, 0, 20);
        scene.idle(20);
        scene.addInstruction(new FadeOutOfSceneInstruction<>(0, Direction.DOWN, wrongHarness)); // Hide immediately
        scene.addInstruction(new DisplayWorldSectionInstruction(0, Direction.NORTH, util.select().position(5, 1, 2), () -> scene.getScene().resolve(mill))); // Show immediately
        scene.addInstruction(new OutlineAABBInstruction(PonderPalette.GREEN, "nice cow", new AABB(5, 1, 2, 7, 2.5, 3), 30));
        scene.idle(40);

        scene.rotateCameraY(-45);
        scene.idle(10);
        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(bearing, Direction.WEST))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(bearing), Pointing.DOWN, 30)
            .rightClick();
        scene.idle(20);
        walk(scene, true);
        scene.world().rotateSection(mill, 0, -170, 0, 200);
        scene.world().rotateBearing(bearing, -170, 200);
        scene.idle(160);

        scene.world().hideSection(util.select().fromTo(0, 0, 6, 2, 0, 8), Direction.DOWN);
        scene.idle(30);
        scene.overlay().showOutlineWithText(util.select().fromTo(0, 0, 6, 2, 0, 8), 60)
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(10);
        walk(scene, false);
        scene.idle(50);
        scene.world().showSection(util.select().fromTo(0, 0, 6, 2, 0, 8), Direction.UP);
        scene.idle(10);
        scene.world().rotateSection(mill, 0, -720, 0, 800);
        scene.world().rotateBearing(bearing, -720, 800);
        walk(scene, true);
        scene.idle(40);

        scene.world().showSection(util.select().fromTo(0, 1, 4, 0, 4, 4).add(util.select().fromTo(1, 4, 4, 4, 4, 4)), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(160)
            .pointAt(util.vector().blockSurface(util.grid().at(0, 4, 4), Direction.WEST))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(60);
        scene.overlay().showText(100)
            .independent(120)
            .colored(PonderPalette.RED)
            .text("This text is defined in a language file");
        scene.idle(120);

        scene.overlay().showText(160)
            .independent(40)
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(60);
        scene.overlay().showText(100)
            .independent(100)
            .colored(PonderPalette.GREEN)
            .text("This text is defined in a language file");
        scene.idle(100);

        scene.markAsFinished();
    };

    private static final BlockPos[] cowHarnesses = new BlockPos[]{new BlockPos(2, 1, 3), new BlockPos(5, 1, 2), new BlockPos(6, 1, 5), new BlockPos(3, 1, 6)};

    private static final void walk(CreateSceneBuilder scene, boolean walking) {
        for (BlockPos pos : cowHarnesses) scene.world().modifyBlockEntity(pos, HarnessWithCowDummyBlockEntity.class, be -> be.walking = walking);
    };
};
