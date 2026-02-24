package com.petrolpark.util;

import com.petrolpark.Petrolpark;

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
