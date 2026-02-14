package com.petrolpark.core.item.decay.drying.rack;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class DryingRackScenes {
    
    public static final void dryingRack(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("drying", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 3);

        final BlockPos rack = util.grid().at(1, 1, 1);
        final Vec3 rackTop  = util.vector().topOf(rack);
        final Vec3 rackSide = util.vector().blockSurface(rack, Direction.WEST);
        final ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
        final ItemStack sponge = new ItemStack(Items.SPONGE);
        final BlockPos hopper = util.grid().at(1, 2, 1);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(rack), Direction.DOWN);
        scene.idle(5);

        scene.overlay().showText(60)
            .attachKeyFrame()
            .pointAt(rackSide)
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.overlay().showControls(rackTop, Pointing.DOWN, 20)
            .rightClick()
            .withItem(wetSponge);
        scene.idle(5);
        scene.world().modifyBlockEntity(rack, DryingRackBlockEntity.class, be -> be.inv.setStackInSlot(0, wetSponge));
        scene.idle(80);
        scene.world().modifyBlockEntity(rack, DryingRackBlockEntity.class, be -> be.inv.setStackInSlot(0, sponge));
        scene.idle(20);
        scene.overlay().showControls(rackTop, Pointing.DOWN, 20)
            .withItem(sponge);
        scene.idle(15);
        scene.overlay().showText(60)
            .attachKeyFrame()
            .pointAt(rackSide)
            .placeNearTarget()
            .text("This text is defined in a language file");
        scene.idle(20);
        scene.overlay().showControls(rackTop, Pointing.DOWN, 20)
            .rightClick();
        scene.idle(5);
        scene.world().modifyBlockEntity(rack, DryingRackBlockEntity.class, be -> be.inv.extractItem(0, 1, false));
        scene.idle(35);
        scene.world().showSection(util.select().position(hopper), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(80)
            .attachKeyFrame()
            .pointAt(util.vector().blockSurface(hopper, Direction.WEST))
            .text("This text is defined in a language file");
        scene.idle(5);
        final ElementLink<EntityElement> item = scene.world().createItemEntity(util.vector().centerOf(1, 5, 1), Vec3.ZERO, wetSponge);
        scene.idle(17);
        scene.world().modifyEntity(item, Entity::discard);
        scene.idle(5);
        scene.world().modifyBlockEntity(rack, DryingRackBlockEntity.class, be -> be.inv.setStackInSlot(0, wetSponge));
        scene.idle(80);
        scene.world().modifyBlockEntity(rack, DryingRackBlockEntity.class, be -> be.inv.setStackInSlot(0, sponge));
    };
};
