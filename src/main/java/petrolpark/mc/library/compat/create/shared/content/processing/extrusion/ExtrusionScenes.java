package petrolpark.mc.library.compat.create.shared.content.processing.extrusion;

import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class ExtrusionScenes {
  
    public static void extrusionDie(SceneBuilder builder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("extrusion_die", "This text is defined in a language file.");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos extrusionDie = util.grid().at(1, 1, 2);

        scene.world().showSection(util.select().position(extrusionDie), Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().position(3, 2, 1), Direction.DOWN);
        scene.world().moveSection(contraption, new Vec3(0, 0, 1), 0);
        scene.world().showSectionAndMerge(util.select().fromTo(4, 2, 1, 5, 2, 1).add(util.select().fromTo(2, 2, 1, 2, 3, 1)), Direction.DOWN, contraption);
        scene.world().showSection(util.select().position(3, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 3, 3, 5).add(util.select().position(2, 0, 5)), Direction.NORTH);
        scene.idle(30);

        BlockPos quartz = util.grid().at(2, 1, 1);
        scene.world().showSectionAndMerge(util.select().position(quartz), Direction.SOUTH, contraption);
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(quartz, Direction.SOUTH), Pointing.UP, 60)
            .withItem(new ItemStack(Blocks.QUARTZ_BLOCK));
        scene.idle(80);

        scene.overlay().showText(200)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(extrusionDie, Direction.SOUTH));
        scene.idle(20);

        Selection redstone1 = util.select().fromTo(2, 2, 1, 2, 3, 1);
        BlockPos sticker = util.grid().at(2, 2, 1);

        scene.world().toggleRedstonePower(redstone1);
        scene.world().modifyBlock(sticker, s -> s.setValue(StickerBlock.EXTENDED, true), false);
		scene.effects().indicateRedstone(util.grid().at(2, 3, 2));
		scene.world().modifyBlockEntityNBT(util.select().position(sticker), StickerBlockEntity.class, nbt -> {});
		scene.idle(20);
		scene.world().toggleRedstonePower(redstone1);
		scene.idle(20);

        scene.world().toggleRedstonePower(util.select().fromTo(3, 2, 4, 3, 3, 4));
        scene.effects().indicateRedstone(util.grid().at(3, 3, 4));
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 2, 3, 2, 3), 16f);
        scene.world().moveSection(contraption, new Vec3(-2, 0, 0), 60);
        scene.idle(45);
        scene.world().setBlock(quartz, Blocks.QUARTZ_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, Axis.X), false);
        scene.effects().emitParticles(util.vector().centerOf(0, 1, 2), scene.effects().particleEmitterWithinBlockSpace(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.QUARTZ_BLOCK.defaultBlockState()), Vec3.ZERO), 10f, 3);
        scene.idle(35);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.NORTH), Pointing.UP, 60)
            .withItem(new ItemStack(Blocks.QUARTZ_PILLAR));
        scene.idle(80);

        scene.overlay().showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .pointAt(util.vector().centerOf(extrusionDie));
        scene.idle(80);

        scene.markAsFinished();
    };
};
