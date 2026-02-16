package com.petrolpark.compat.create.common.processing.centrifuge;

import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
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
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CentrifugeScenes {
  
    public static final void centrifuge(SceneBuilder sceneBuilder, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneBuilder);
        scene.title("centrifuge", "This text is defined in a language file");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        final BlockPos centrifuge = util.grid().at(2, 3, 2);
        final BlockPos bottom = util.grid().at(2, 1, 2);
        final BlockPos funnel = util.grid().at(2, 3, 1);
        final Vec3 bottomSide = util.vector().blockSurface(bottom, Direction.NORTH);

        final FluidStack harmingPotion = PotionFluid.of(1000, new PotionContents(Potions.HARMING), BottleType.REGULAR);
        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.particleOffset = util.vector().of(0d, -2d, 0d));
        scene.world().modifyBlockEntity(util.grid().at(2, 5, 2), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(harmingPotion, FluidAction.EXECUTE));

        scene.idle(2);
        scene.world().showSection(util.select().position(1, 0, 5), Direction.NORTH);
        scene.idle(2);
        scene.world().showSection(util.select().position(1, 1, 4), Direction.DOWN);
        scene.idle(2);
        scene.world().showSection(util.select().position(2, 1, 3), Direction.DOWN);
        scene.idle(5);
        final ElementLink<WorldSectionElement> centrifugeS = scene.world().showIndependentSection(util.select().position(2, 3, 2), Direction.DOWN);
        scene.world().moveSection(centrifugeS, util.vector().of(0d, -2d, 0d), 0);
        scene.idle(10);
        scene.overlay().showText(40)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(bottom, Direction.WEST))
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(50);
        scene.overlay().showText(130)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(bottom, Direction.UP))
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().showSection(util.select().position(2, 2, 3), Direction.DOWN);
        final ElementLink<WorldSectionElement> topPump = scene.world().showIndependentSection(util.select().fromTo(2, 4, 2, 2, 5, 2), Direction.DOWN);
        scene.world().moveSection(topPump, util.vector().of(0d, -2d, 0d), 0);
        scene.idle(80);
        scene.overlay().showControls(bottomSide, Pointing.RIGHT, 30)
            .withItem(PotionContents.createItemStack(Items.POTION, Potions.HARMING));
        scene.idle(120);

        scene.world().modifyBlockEntity(centrifuge, CentrifugeBlockEntity.class, be -> be.inputTank.getCapability().drain(1000, FluidAction.EXECUTE));
        scene.idle(20);
        final ItemStack poisonBottle = PotionContents.createItemStack(Items.POTION, Potions.POISON);
        scene.overlay().showControls(bottomSide, Pointing.RIGHT, 30)
            .withItem(poisonBottle);
        scene.overlay().showControls(util.vector().blockSurface(bottom, Direction.WEST), Pointing.LEFT, 30)
            .withItem(new ItemStack(Items.FERMENTED_SPIDER_EYE));
        scene.idle(40);
        scene.world().hideIndependentSection(topPump, Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 3, 3), Direction.DOWN);
        scene.idle(15);
        scene.world().moveSection(centrifugeS, util.vector().of(0, 2d, 0), 10);
        scene.idle(10);
        scene.overlay().showText(115)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(util.grid().at(2, 3, 2), Direction.WEST))
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 0, 2, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 3, 2), Direction.EAST);
        scene.idle(40);
        final FluidStack poisonPotion = PotionFluid.of(1000, new PotionContents(Potions.POISON), BottleType.REGULAR);
        scene.world().modifyBlockEntity(util.grid().at(0, 1, 2), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(poisonPotion, FluidAction.EXECUTE));
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.NORTH), Pointing.RIGHT, 30)
            .withItem(poisonBottle);
        scene.idle(60);

        scene.overlay().showText(60)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(centrifuge, Direction.DOWN))
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(bottom, bottom.above()), Direction.SOUTH);
        scene.idle(60);

        scene.world().showSection(util.select().position(funnel), Direction.SOUTH);
        scene.idle(10);
        scene.overlay().showText(60)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(centrifuge, Direction.NORTH))
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.world().flapFunnel(funnel, true);
        scene.world().createItemEntity(util.vector().centerOf(funnel), Vec3.ZERO, new ItemStack(Items.FERMENTED_SPIDER_EYE));
    };
};
