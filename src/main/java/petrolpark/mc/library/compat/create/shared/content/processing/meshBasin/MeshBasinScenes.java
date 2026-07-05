package petrolpark.mc.library.compat.create.shared.content.processing.meshBasin;

import java.util.function.Supplier;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateFluids;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedItems;

public class MeshBasinScenes {

    /**
     * Other mods should change this if {@link SharedFeatureFlag#SUNFLOWER_OIL} and {@link SharedFeatureFlag#FRIES} are not enabled
     */
    public static ItemLike toFry = Items.COD;
    public static Supplier<FluidStack> fryingFluid = () -> new FluidStack(Fluids.WATER, 1000);
    public static ItemLike fried = Items.COOKED_COD;

    /**
     * Other mods should change this if {@link SharedFeatureFlag#SUNFLOWER_OIL} is not enabled
     */
    public static ItemLike toJuice = Items.SUNFLOWER;
    public static Supplier<FluidStack> juicedFluid = () -> new FluidStack(Fluids.WATER, 1000);

    /**
     * Other mods should change this
     */
    public static Supplier<FluidStack> toFilter = () -> new FluidStack(Fluids.WATER, 1000);
    public static ItemLike residue = Items.MUD;
    public static Supplier<FluidStack> filtrate = () -> new FluidStack(Fluids.WATER, 750);
    
    static {
        if (SharedFeatureFlag.SUNFLOWER_OIL.enabled()) {
            fryingFluid = () -> new FluidStack(SharedCreateFluids.SUNFLOWER_OIL.get(), 1000);
            juicedFluid = () -> new FluidStack(SharedCreateFluids.SUNFLOWER_OIL.get(), 1000);
        };
        if (SharedFeatureFlag.FRIES.enabled()) {
            toFry = SharedItems.RAW_FRIES;
            fried = SharedItems.FRIES;
        };
    };

    public static final void boiling(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
		scene.title("boiling", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 3);

        final BlockPos basin = util.grid().at(1, 2, 1);
        final BlockPos press = util.grid().at(1, 4, 1);
        final ItemStack toFryStack = new ItemStack(toFry);
        final ItemStack friedStack = new ItemStack(fried);

        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> {
            be.getItemHandler(null).insertItem(0, toFryStack, false);
            be.getFluidHandler(null).fill(fryingFluid.get().copy(), FluidAction.EXECUTE);
        });

        scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
			.pointAt(util.vector().blockSurface(basin, Direction.WEST))
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
        scene.idle(40);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 30)
            .withItem(toFryStack);
        scene.idle(50);

        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> be.selfProcessingTicksRemaining = 100);
        scene.idle(100);
        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> {
            be.getItemHandler(null).setStackInSlot(0, friedStack);
            be.getFluidHandler(null).drain(1000, FluidAction.EXECUTE);
        });
        scene.idle(10);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 30)
            .withItem(friedStack);
        scene.idle(50);

        scene.world().showIndependentSection(util.select().position(press), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showOutlineWithText(util.select().position(press), 60)
            .colored(PonderPalette.RED)
            .placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
        scene.idle(60);
    };

    public static final void juicing(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
		scene.title("juicing", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 5);

        final BlockPos basin = util.grid().at(1, 2, 3);
        final BlockPos press = util.grid().at(1, 4, 3);
        final Vec3 basinSide = util.vector().blockSurface(basin, Direction.WEST);
        final ItemStack toJuiceStack = new ItemStack(toJuice);

        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> {
            be.getItemHandler(null).insertItem(0, toJuiceStack, false);
        });

		scene.showBasePlate();
        scene.idle(5);
        scene.world().showSection(util.select().position(1, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().position(2, 0, 5), Direction.NORTH);
        scene.idle(2);
        scene.world().showSection(util.select().position(1, 1, 5), Direction.NORTH);
        for (int y = 1; y <=4; y++) {
            scene.idle(2);
            scene.world().showSection(util.select().position(1, y, 4), Direction.DOWN);
        };
        scene.world().showSection(util.select().position(press), Direction.SOUTH);
        scene.idle(20);

        scene.overlay().showText(70)
			.pointAt(basinSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(40);
        scene.overlay().showControls(util.vector().topOf(basin), Pointing.DOWN, 30)
            .withItem(toJuiceStack);
        scene.idle(30);
        scene.world().modifyBlockEntity(press, MechanicalPressBlockEntity.class, pte -> 
            pte.getPressingBehaviour().start(PressingBehaviour.Mode.valueOf("PETROLPARK_MESH_BASIN"))
        );
		scene.idle(30);
		scene.world().modifyBlockEntity(press, MechanicalPressBlockEntity.class, pte -> 
            pte.getPressingBehaviour().makeCompactingParticleEffect(util.vector().centerOf(basin), toJuiceStack)
        );
        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> {
            be.getItemHandler(null).extractItem(0, 1, false);
            be.getFluidHandler(null).fill(juicedFluid.get().copy(), FluidAction.EXECUTE);
        });
        scene.idle(40);
        scene.world().showSection(util.select().fromTo(2, 1, 1, 3, 2, 5), Direction.WEST);
        scene.idle(10);
        scene.world().propagatePipeChange(util.grid().at(2, 1, 2));
        scene.idle(60);

        scene.rotateCameraY(-30);
		scene.idle(10);
		scene.world().setBlock(util.grid().at(1, 1, 3), AllBlocks.BLAZE_BURNER.getDefaultState()
			.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.KINDLED), true);
		scene.idle(10);

        scene.overlay().showText(80)
			.pointAt(basinSide.subtract(0d, 1d, 0d))
            .attachKeyFrame()
			.placeNearTarget()
			.text("This text is defined in a language file");
		scene.idle(40);

		scene.rotateCameraY(30);

		scene.idle(60);
		Vec3 filterPos = util.vector().of(1, 2.75f, 3.5f);
		scene.overlay().showFilterSlotInput(filterPos, Direction.WEST, 100);
		scene.overlay().showText(100)
			.pointAt(filterPos)
			.placeNearTarget()
			.attachKeyFrame()
			.text("This text is defined in a language file");
		scene.idle(80);
    };

    public static final void filtering(SceneBuilder sceneIn, SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(sceneIn);
		scene.title("filtering", "This text is defined in a language file");
		scene.configureBasePlate(0, 0, 5);

        final ItemStack residueStack = new ItemStack(residue);
        final BlockPos spout = util.grid().at(2, 4, 2);
        final BlockPos basin = util.grid().at(2, 2, 2);

        scene.world().modifyBlockEntity(util.grid().at(4, 1, 2), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(toFilter.get(), FluidAction.EXECUTE));

        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(basin), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(spout), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 2, 4, 4, 5).add(util.select().position(3, 0, 5)), Direction.WEST);
        scene.idle(10);
        
        scene.overlay().showText(100)
            .pointAt(util.vector().blockSurface(spout, Direction.WEST))
            .attachKeyFrame()
            .text("This text is defined in a language file");
        scene.idle(10);
        scene.world().propagatePipeChange(util.grid().at(4, 3, 2));
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(util.select().position(spout), SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", SpoutBlockEntity.FILLING_TIME));
        scene.idle(10);
        scene.world().modifyBlockEntity(basin, MeshBasinBlockEntity.class, be -> {
            be.getItemHandler(null).insertItem(0, residueStack, false);
            be.getFluidHandler(null).fill(filtrate.get().copy(), FluidAction.EXECUTE);
        });
        scene.idle(60);

        scene.rotateCameraY(-30);
        scene.idle(10);
        scene.world().setBlock(util.grid().at(2, 1, 2), AllBlocks.BLAZE_BURNER.getDefaultState()
			.setValue(BlazeBurnerBlock.HEAT_LEVEL, HeatLevel.KINDLED), true);
		scene.idle(10);

        scene.overlay().showText(80)
			.pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.WEST))
            .attachKeyFrame()
			.placeNearTarget()
			.text("This text is defined in a language file");
		scene.idle(40);

		scene.rotateCameraY(30);

		scene.idle(60);
		Vec3 filterPos = util.vector().of(2f, 2.75f, 2.5f);
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
