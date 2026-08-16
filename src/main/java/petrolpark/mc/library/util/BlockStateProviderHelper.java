package petrolpark.mc.library.util;

import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import petrolpark.mc.library.Petrolpark;

public class BlockStateProviderHelper {

    public static final void randomizedRotationLogBlock(BlockStateProvider prov, RotatedPillarBlock block) {
        randomizedRotationAxisBlock(prov, block, prov.blockTexture(block), extend(prov.blockTexture(block), "_top"));
    };

    public static final void randomizedRotationAxisBlock(BlockStateProvider prov, RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        randomizedRotationAxisBlock(prov, block,
            prov.models().cubeColumn(name(block), side, end),
            cubeColumnSideways(prov.models(), name(block) + "_sideways", side, end)
        );
    };
    
    public static final void randomizedRotationAxisBlock(BlockStateProvider prov, RotatedPillarBlock block, ModelFile vertical, ModelFile sideways) {
        prov.getVariantBuilder(block)
            .partialState().with(RotatedPillarBlock.AXIS, Axis.Y).modelForState()
                .modelFile(vertical).nextModel()
                .modelFile(vertical).rotationY(90).nextModel()
                .modelFile(vertical).rotationY(180).nextModel()
                .modelFile(vertical).rotationY(270).addModel()
            .partialState().with(RotatedPillarBlock.AXIS, Axis.Z).modelForState()
                .modelFile(sideways).rotationY(90).nextModel()
                .modelFile(sideways).rotationX(90).rotationY(90).nextModel()
                .modelFile(sideways).rotationX(180).rotationY(90).nextModel()
                .modelFile(sideways).rotationX(270).rotationY(90).addModel()
            .partialState().with(RotatedPillarBlock.AXIS, Axis.X).modelForState()
                .modelFile(sideways).nextModel()
                .modelFile(sideways).rotationX(90).nextModel()
                .modelFile(sideways).rotationX(180).nextModel()
                .modelFile(sideways).rotationX(270).addModel();
    };

    public static final <T extends ModelBuilder<T>> T cubeColumnSideways(ModelProvider<T> prov, String name, ResourceLocation side, ResourceLocation end) {
        return prov.getBuilder(name).parent(new UncheckedModelFile(Petrolpark.asResource("block/cube_column_sideways")))
            .texture("side", side)
            .texture("end", end);
    };

    /**
     * Adds a rotated {@link net.neoforged.neoforge.client.model.generators.ConfiguredModel ConfiguredModel} for every
     * {@link Orientation#EDGE_ORIENTATIONS EDGE_ORIENTATION}, against {@link Orientation#EDGE_ORIENTATION_PROPERTY},
     * using a single {@code model} authored as {@link Orientation#EAST_SOUTH EAST_SOUTH}.
     * <p>
     * {@link Orientation#blockStateXRotation} and {@link Orientation#blockStateYRotation} render an orientation truly, from either {@code UP_SOUTH} or
     * {@code EAST_SOUTH} depending which is vertical - but {@code model} here is only ever {@code EAST_SOUTH}. So
     * for the 4 edges whose fields are {@code UP_SOUTH}-relative ({@code UP_SOUTH}, {@code UP_NORTH},
     * {@code DOWN_SOUTH}, {@code DOWN_NORTH}), we instead use their front/top-swapped counterpart's fields (e.g.
     * {@code SOUTH_UP} instead of {@code UP_SOUTH}), which are {@code EAST_SOUTH}-relative. The block will still
     * occupy the correct edge, but for those 4 states {@code model}'s "front" and "top" faces are swapped relative to
     * its true orientation, so its roll around that edge will differ from the other 8. If that matters, use
     * {@link #orientedBlock(VariantBlockStateBuilder, ModelFile, ModelFile)} instead, which preserves true
     * orientation at the cost of a second model.
     */
    public static final VariantBlockStateBuilder edgeOrientedBlock(VariantBlockStateBuilder builder, ModelFile model) {
        for (Orientation state : Orientation.EDGE_ORIENTATIONS) {
            Orientation rotation = state.asEdgeBlockStateRotation();
            builder = builder.partialState().with(Orientation.EDGE_ORIENTATION_PROPERTY, state).modelForState()
                .modelFile(model).rotationX(rotation.blockStateXRotation).rotationY(rotation.blockStateYRotation).addModel();
        };
        return builder;
    };

    /**
     * Adds a rotated {@link net.neoforged.neoforge.client.model.generators.ConfiguredModel ConfiguredModel} for every
     * {@link Orientation}, against {@link Orientation#ORIENTATION_PROPERTY}, preserving true orientation (front/top
     * are never swapped, unlike {@link #edgeOrientedBlock(VariantBlockStateBuilder, ModelFile)}).
     * <p>
     * {@code upSouthModel} must be authored as {@link Orientation#UP_SOUTH UP_SOUTH} and {@code eastSouthModel} as
     * {@link Orientation#EAST_SOUTH EAST_SOUTH} - each orientation's {@link Orientation#blockStateXRotation} and {@link Orientation#blockStateYRotation}
     * are relative to whichever of those 2 is vertical, so we pick the matching model per orientation.
     */
    public static final VariantBlockStateBuilder orientedBlock(VariantBlockStateBuilder builder, ModelFile upSouthModel, ModelFile eastSouthModel) {
        for (Orientation state : Orientation.values()) {
            ModelFile model = state.isTopVertical() ? upSouthModel : eastSouthModel;
            builder = builder.partialState().with(Orientation.ORIENTATION_PROPERTY, state).modelForState()
                .modelFile(model).rotationX(state.blockStateXRotation).rotationY(state.blockStateYRotation).addModel();
        };
        return builder;
    };

    private static final ResourceLocation extend(ResourceLocation rl, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
    };

    private static final ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    };

    private static final String name(Block block) {
        return key(block).getPath();
    };
};
