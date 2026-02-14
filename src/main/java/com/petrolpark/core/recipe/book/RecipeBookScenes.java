package com.petrolpark.core.recipe.book;

import com.petrolpark.PetrolparkItems;
import com.petrolpark.RequiresCreate;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

public class RecipeBookScenes {
    
    public static final void recipeBook(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("recipe_book", "This text is defined in a language file");
        scene.configureBasePlate(0, 0, 5);

        final BlockPos middle = util.grid().at(2, 1, 2);
        final BlockPos shelf = util.grid().at(1, 1, 2);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(middle), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(middle, Direction.WEST))
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.overlay().showOutline(PonderPalette.RED, middle, util.select().position(middle), 60);
        scene.idle(70);

        final ItemStack book = PetrolparkItems.RECIPE_BOOK.asStack();

        scene.world().showSection(util.select().position(shelf), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(40)
            .pointAt(util.vector().blockSurface(shelf, Direction.WEST))
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(50);
        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(shelf, Direction.WEST))
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(shelf), Pointing.DOWN, 30)
            .rightClick()
            .withItem(book);
        scene.idle(5);
        scene.world().modifyBlockEntity(shelf, ChiseledBookShelfBlockEntity.class, be -> {
            be.setItem(1, book);
        });
        scene.world().setBlock(shelf, Blocks.CHISELED_BOOKSHELF.defaultBlockState().setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(1), true), false);
        scene.idle(50);

        scene.overlay().showText(40)
            .pointAt(util.vector().blockSurface(middle, Direction.WEST))
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.overlay().showOutline(PonderPalette.GREEN, middle, util.select().position(middle), 40);
        scene.idle(50);

        scene.overlay().showText(120)
            .pointAt(util.vector().blockSurface(middle, Direction.NORTH))
            .attachKeyFrame()
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().hideSection(util.select().position(middle), Direction.NORTH);
        scene.idle(15);

        final ElementLink<WorldSectionElement> furnace = scene.world().showIndependentSection(util.select().position(3, 1, 2), Direction.NORTH);
        scene.world().moveSection(furnace, util.vector().of(-1d, 0d, 0d), 0);
        scene.idle(20);
        scene.world().hideIndependentSection(furnace, Direction.NORTH);
        scene.idle(15);

        final ElementLink<WorldSectionElement> crafter = scene.world().showIndependentSection(util.select().position(4, 1, 2), Direction.NORTH);
        scene.world().moveSection(crafter, util.vector().of(-2d, 0d, 0d), 0);
        scene.idle(20);
        scene.world().hideIndependentSection(crafter, Direction.NORTH);
        scene.idle(15);

        final ElementLink<WorldSectionElement> stonecutter = scene.world().showIndependentSection(util.select().position(0, 1, 2), Direction.NORTH);
        scene.world().moveSection(stonecutter, util.vector().of(2d, 0d, 0d), 0);
        scene.idle(20);
    };

    @RequiresCreate
    public static final void create(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("recipe_book_create", "This text is defined in a language file");
        scene.configureBasePlate(0, 0, 5);

        final BlockPos basin = util.grid().at(2, 1, 2);
        final BlockPos mixer = util.grid().at(2, 3, 2);
        final Selection verticalShaft = util.select().fromTo(3, 1, 2, 3, 3, 2);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(3, 1, 3, 3, 1, 5), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(verticalShaft, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(mixer), Direction.EAST);
        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.SOUTH);
        scene.idle(5);

        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(mixer, Direction.WEST))
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(20);
        final ElementLink<WorldSectionElement> shelf = scene.world().showIndependentSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.idle(60);

        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(mixer, Direction.WEST))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().moveSection(shelf, util.vector().of(0d, 2d, 0d), 10);
        scene.idle(30);
        scene.rotateCameraY(-30f);
        scene.idle(10);
        scene.world().hideSection(verticalShaft, Direction.UP);
        scene.world().hideSection(util.select().position(mixer), Direction.UP);
        scene.idle(15);
        ElementLink<WorldSectionElement> cog = scene.world().showIndependentSection(util.select().position(3, 1, 0), Direction.SOUTH);
        scene.world().moveSection(cog, util.vector().of(0d, 0d, 2d), 0);
        final ElementLink<WorldSectionElement> press = scene.world().showIndependentSection(util.select().fromTo(2, 2, 4, 3, 3, 5), Direction.SOUTH);
        scene.world().moveSection(press, util.vector().of(0d, 0d, -3d), 0);
        scene.idle(30);
        scene.rotateCameraY(30f);
        scene.idle(10);
        scene.world().hideIndependentSection(press, Direction.UP);
        scene.world().hideSection(util.select().position(basin), Direction.UP);
        scene.idle(10);
        scene.world().moveSection(shelf, util.vector().of(-1d, 1d, 0d), 10);
        scene.idle(10);
        final ElementLink<WorldSectionElement> crafters = scene.world().showIndependentSection(util.select().fromTo(1, 2, 0, 3, 4 ,0), Direction.SOUTH);
        scene.world().moveSection(crafters, util.vector().of(0d, 0d, 2d), 0);
        scene.idle(15);
        scene.world().moveSection(shelf, util.vector().of(0d, -1d, 0d), 10);
        scene.idle(20);
        scene.world().moveSection(shelf, util.vector().of(0d, -1d, 0d), 10);
        scene.idle(20);
    };
};
