package com.petrolpark.compat.create.core.client.offGridTiling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class OffGridTilingModel extends BakedModelWrapperWithData {

    private static final ModelProperty<BlockPos> POS_PROPERTY = new ModelProperty<>();

    public OffGridTilingModel(BakedModel originalModel) {
        super(originalModel);
    };

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        return builder.with(POS_PROPERTY, pos);
    };

    @Override
    @SuppressWarnings("null")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (!extraData.has(POS_PROPERTY)) return quads;
        final BlockPos pos = extraData.get(POS_PROPERTY);
        quads = new ArrayList<>(quads);

        for (int i = 0; i < quads.size(); i++) {
            final BakedQuad quad = quads.get(i);

            final TextureAtlasSprite sprite = quad.getSprite();
			final Optional<OffGridTilingMetadataSection> sectionOp = sprite.contents().metadata().getSection(OffGridTilingMetadataSection.TYPE);
            if (sectionOp.isEmpty()) continue;
            final OffGridTilingMetadataSection section = sectionOp.get();

            final Direction face = quad.getDirection();
            final float uShift = ((float)getHorizontalPosition(pos, face) % section.xSize()) / section.scale();
            final float vShift = ((float)getVerticalPosition(pos, face) % section.ySize()) / section.scale();

            final BakedQuad newQuad = BakedQuadHelper.clone(quad);
            final int[] vertexData = newQuad.getVertices();

			for (int vertex = 0; vertex < 4; vertex++) {
				float u = BakedQuadHelper.getU(vertexData, vertex);
				float v = BakedQuadHelper.getV(vertexData, vertex);
				BakedQuadHelper.setU(vertexData, vertex, sprite.getU(SpriteShiftEntry.getUnInterpolatedU(sprite, u) + uShift));
				BakedQuadHelper.setV(vertexData, vertex, sprite.getV(SpriteShiftEntry.getUnInterpolatedV(sprite, v) + vShift));
			};

            quads.set(i, newQuad);
        };

        return quads;
    };

    protected static int getVerticalPosition(@Nonnull BlockPos pos, @Nonnull Direction face) {
		return face.getAxis().isHorizontal() ? -pos.getY() : pos.getZ();
	};

	protected static int getHorizontalPosition(@Nonnull BlockPos pos, @Nonnull Direction face) {
		return face.getAxis() == Axis.X ? pos.getZ() : pos.getX();
	};
    
};
