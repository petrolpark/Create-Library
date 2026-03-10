package com.petrolpark.client.rendering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Copied from {@link com.simibubi.create.foundation.model.BakedModelHelper Create source code}.
 */
public class BakedModelHelper {

	public static final BakedQuad copyWithSprite(BakedQuad quad, TextureAtlasSprite sprite) {
		return new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade(), quad.hasAmbientOcclusion());
	};

	@SuppressWarnings("null")
	public static final TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
		final BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		if (model == null) return null;
		final RandomSource random = RandomSource.create();
		random.setSeed(42l);
		List<BakedQuad> quads = model.getQuads(state, side, random, ModelData.EMPTY, null);
		if (!quads.isEmpty()) return quads.get(0).getSprite();
		
		random.setSeed(42l);
		quads = model.getQuads(state, null, random, ModelData.EMPTY, null);
		if (!quads.isEmpty()) {
			for (BakedQuad quad : quads) {
				if (quad.getDirection() == side) return quad.getSprite();
			};
		};
		return model.getParticleIcon(ModelData.EMPTY);
	};
  
    @SuppressWarnings({"null", "deprecation"})
    public static BakedModel swapSprites(BakedModel template, UnaryOperator<TextureAtlasSprite> spriteSwapper) {
		final RandomSource random = RandomSource.create();

		final Map<Direction, List<BakedQuad>> culledFaces = new EnumMap<>(Direction.class);
		for (Direction cullFace : Iterate.directions) {
			random.setSeed(42L);
			List<BakedQuad> quads = template.getQuads(null, cullFace, random, ModelData.EMPTY, RenderType.solid());
			culledFaces.put(cullFace, swapSprites(quads, spriteSwapper));
		};

		random.setSeed(42L);
		final List<BakedQuad> quads = template.getQuads(null, null, random, ModelData.EMPTY, RenderType.solid());
		final List<BakedQuad> unculledFaces = swapSprites(quads, spriteSwapper);

		TextureAtlasSprite particleSprite = template.getParticleIcon(ModelData.EMPTY);
		final TextureAtlasSprite swappedParticleSprite = spriteSwapper.apply(particleSprite);
		if (swappedParticleSprite != null) {
			particleSprite = swappedParticleSprite;
		};

		return new SimpleBakedModel(unculledFaces, culledFaces, template.useAmbientOcclusion(), template.usesBlockLight(), template.isGui3d(), particleSprite, template.getTransforms(), template.getOverrides());
	};

    public static List<BakedQuad> swapSprites(List<BakedQuad> quads, UnaryOperator<TextureAtlasSprite> spriteSwapper) {
		final List<BakedQuad> newQuads = new ArrayList<>(quads);
		final int size = quads.size();
		for (int i = 0; i < size; i++) {
			final BakedQuad quad = quads.get(i);
			final TextureAtlasSprite sprite = quad.getSprite();
			final TextureAtlasSprite newSprite = spriteSwapper.apply(sprite);
			if (newSprite == null || sprite == newSprite) continue;

			final BakedQuad newQuad = clone(quad);
			final int[] vertexData = newQuad.getVertices();

			for (int vertex = 0; vertex < 4; vertex++) {
				final float u = getU(vertexData, vertex);
				final float v = getV(vertexData, vertex);
				setU(vertexData, vertex, newSprite.getU(SpriteShiftEntry.getUnInterpolatedU(sprite, u)));
				setV(vertexData, vertex, newSprite.getV(SpriteShiftEntry.getUnInterpolatedV(sprite, v)));
			};

			newQuads.set(i, newQuad);
		};
		return newQuads;
	};

	public static final BakedQuad clone(BakedQuad quad) {
		return new BakedQuad(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
	};

	public static final VertexFormat FORMAT = DefaultVertexFormat.BLOCK;
	public static final int VERTEX_STRIDE = FORMAT.getVertexSize() / 4;
	public static final int U_OFFSET = 4;
	public static final int V_OFFSET = 5;

	public static final float getU(int[] vertexData, int vertex) {
		return Float.intBitsToFloat(vertexData[vertex * VERTEX_STRIDE + U_OFFSET]);
	};

	public static final float getV(int[] vertexData, int vertex) {
		return Float.intBitsToFloat(vertexData[vertex * VERTEX_STRIDE + V_OFFSET]);
	};

	public static final void setU(int[] vertexData, int vertex, float u) {
		vertexData[vertex * VERTEX_STRIDE + U_OFFSET] = Float.floatToRawIntBits(u);
	};

	public static final void setV(int[] vertexData, int vertex, float v) {
		vertexData[vertex * VERTEX_STRIDE + V_OFFSET] = Float.floatToRawIntBits(v);
	};
};
