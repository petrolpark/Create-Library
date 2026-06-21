package petrolpark.mc.library.compat.create.shared.content.processing.blender;

import com.google.common.collect.ImmutableList;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateFluids;
import petrolpark.mc.library.core.client.ponder.instruction.KillEntityInstruction;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;

public class BlenderScenes {

    /**
     * Other mods should change this
     */
    public static ItemLike toBlend = Items.BARRIER;
    public static ItemLike blended = Items.BARRIER;
    
    public static final void blending(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
		scene.title("blending", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 5);

        final BlockPos blender = util.grid().at(1, 1, 2);
        final BlockPos basin = blender.above();
        final ItemStack toBlendStack = new ItemStack(toBlend);
        final ItemStack blendedStack = new ItemStack(blended);

        setBasinStack(scene, util, basin, toBlendStack);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(blender), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 3, 1, 1), Direction.WEST);
        scene.world().showSection(util.select().fromTo(3, 1, 2, 3, 1, 5), Direction.WEST);
        scene.idle(10);

        scene.overlay().showText(70)
            .pointAt(util.vector().blockSurface(basin, Direction.WEST))
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(40);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 30)
            .withItem(toBlendStack);
        scene.idle(50);

        scene.world().modifyBlockEntity(blender, BlenderBlockEntity.class, be -> be.processingTicksRemaining = 100);
        scene.idle(100);
        scene.world().modifyBlockEntityNBT(util.select().position(basin), BasinBlockEntity.class, nbt -> {
			nbt.put("VisualizedItems",
				NBTHelper.writeCompoundList(ImmutableList.of(IntAttached.with(1, blendedStack)), ia -> (CompoundTag) ia.getValue().saveOptional(scene.world().getHolderLookupProvider())));
		});
        scene.idle(4);
		scene.world().createItemOnBelt(util.grid().at(1, 1, 1), Direction.UP, blendedStack);
        scene.idle(50);

		final Vec3 filterPos = util.vector().of(1, 2.75f, 2.5f);
		scene.overlay().showFilterSlotInput(filterPos, Direction.WEST, 100);
		scene.overlay().showText(100)
			.pointAt(filterPos)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(80);
    };

    @SuppressWarnings("null")
    public static final void blood(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
		scene.title("blender_blood", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 5);

        final BlockPos basin = util.grid().at(2, 2, 2);

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(3, 0, 5), Direction.NORTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(3, 1, 4), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(2, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(basin.below(), basin), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(60)
            .pointAt(util.vector().blockSurface(basin, Direction.WEST))
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(20);

        final ElementLink<EntityElement> victim = scene.world().createEntity(level -> {
            final Villager entity = EntityType.VILLAGER.create(level);
            final Vec3 p = util.vector().topOf(util.grid().at(2, 1, 2)).add(0d, 0.25d, 0d);
			entity.setPos(p.x, p.y, p.z);
			entity.xo = p.x;
			entity.yo = p.y;
			entity.zo = p.z;
			WalkAnimationState animation = entity.walkAnimation;
			animation.update(-animation.position(), 1f);
			animation.setSpeed(1);
			entity.yRotO = 145;
			entity.setYRot(145f);
            entity.yBodyRot = entity.yBodyRotO = entity.yHeadRot = entity.yHeadRotO = 145f;
            entity.setXRot(-25f);
            entity.xRotO = -25f;
            return entity;
        });

        for (int i = 0; i < 5; i++) {
            scene.idle(20);
            scene.world().modifyEntity(victim, entity -> {
                ((Villager)entity).hurtTime = 10;
            });
            scene.addInstruction(s -> s.getWorld().getCapability(Capabilities.FluidHandler.BLOCK, basin, null).fill(new FluidStack(SharedCreateFluids.BLOOD.get(), 200), FluidAction.EXECUTE));
        };
        scene.addInstruction(new KillEntityInstruction(victim));

        scene.idle(20);
        scene.overlay().showText(60)
            .pointAt(util.vector().topOf(basin))
            .placeNearTarget()
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(60);
    };

    @SuppressWarnings("null")
    public static final void setBasinStack(SceneBuilder builder, SceneBuildingUtil util, BlockPos basinPos, ItemStack stack) {
        builder.addInstruction(scene -> {
            IItemHandler cap = scene.getWorld().getCapability(Capabilities.ItemHandler.BLOCK, basinPos, null);
            cap.extractItem(0, 64, false);
            if (cap != null) cap.insertItem(0, stack, false);
        });
    };
};
