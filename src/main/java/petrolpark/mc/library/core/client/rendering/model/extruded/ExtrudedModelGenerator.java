package petrolpark.mc.library.core.client.rendering.model.extruded;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.joml.Vector3f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import petrolpark.mc.library.util.Mask;
import petrolpark.mc.library.util.MathsHelper;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Largely copied from {@link ItemModelGenerator}.
 * @see ExtrudedModelGenerator#generateExtrudedModel(Mask, float, float, BlockFaceUV, Function, BlockModel)
 */
public class ExtrudedModelGenerator {

    public static final String TOP_TEXTURE_KEY = "top";
    public static final String SIDE_TEXTURE_KEY = "side";
    public static final String BOTTOM_TEXTURE_KEY = "bottom";

    protected static final List<String> REQUIRED_TEXTURE_KEYS = List.of(TOP_TEXTURE_KEY, SIDE_TEXTURE_KEY, BOTTOM_TEXTURE_KEY);

    /**
     * Generate an {@link ExtrudedModel}. The extrusion takes place in the z direction.
     * @param mask The {@link Mask} to use
     * @param minZ
     * @param maxZ Should be > {@code minZ}
     * @param topUV The UV coordinates for the top texture, were the {@code mask} to just cover the whole Block (i.e. {@code (0, 0)} to {@code (16, 16)}).
     * This will be used to decide how to map the {@code top} and {@code bottom} textures to the resulting south and north faces.
     * @param spriteGetter
     * @param baseModel A {@link BlockModel} containing no {@link BlockElement elements}, but the {@link BlockModel#textureMap specifying the textures} {@code top}, {@code side} and {@code bottom}.
     * The {@code side} texture should be 16x16 and will be tiled, but the top and bottom textures will not be, so they need to be large enough for the {@code topUV}, when fit to the {@code mask}, to still fit.
     * Can optionally specify textures {@code top_1}, {@code top_2}, etc. that will be layered on top with the corresponding tint index
     * ({@code bottom}, {@code side} and {@code top} will have tint index {@code 0}).
     */
    public BlockModel generateExtrudedModel(Mask mask, float minZ, float maxZ, BlockFaceUV topUV, Function<Material, TextureAtlasSprite> spriteGetter, BlockModel baseModel) {
        final Map<String, Either<Material, String>> textures = Maps.newHashMap();
        final List<BlockElement> elements = Lists.newArrayList();

        for (String textureKey : REQUIRED_TEXTURE_KEYS) {
            if (!baseModel.hasTexture(textureKey)) {
                if (textureKey == BOTTOM_TEXTURE_KEY) textureKey = TOP_TEXTURE_KEY;
                else throw new IllegalArgumentException("Missing texture: "+textureKey);
            };
            final Material material = baseModel.getMaterial(textureKey);
            textures.put(textureKey, Either.left(material));
        };

        for (Map.Entry<String, Either<Material, String>> additionalTextureEntry : baseModel.textureMap.entrySet()) {
            if (REQUIRED_TEXTURE_KEYS.contains(additionalTextureEntry.getKey())) continue;
            textures.put(additionalTextureEntry.getKey(), additionalTextureEntry.getValue());
        };

        elements.addAll(getExtrudedSideElements(mask, minZ, maxZ));
        elements.addAll(getMaskedFrontElements(mask, minZ, maxZ, topUV, 0, TOP_TEXTURE_KEY, 16f, 16f));

        int i = 1;
        while (baseModel.hasTexture("top_" + i)) {
            elements.addAll(getMaskedFrontElements(mask, minZ, maxZ, topUV, i, "top_" + i, 16f, 16f));
            i++;
        };

        final BlockModel model = new BlockModel(null, elements, textures, false, baseModel.getGuiLight(), baseModel.getTransforms(), baseModel.getOverrides());
        model.name = baseModel.name;
        model.customData.copyFrom(baseModel.customData);
        model.customData.setGui3d(false);

        return model;
    };

    private List<BlockElement> getMaskedFrontElements(Mask mask, float minZ, float maxZ, BlockFaceUV topUV, int tintIndex, String textureKey, float textureWidth, float textureHeight) {
        final List<BlockElement> elements = Lists.newArrayList();
        final float uOffset = topUV.uvs[0];
        final float vOffset = topUV.uvs[1];
        final float uScale = (topUV.uvs[2] - uOffset) / 16f;
        final float vScale = (topUV.uvs[3] - vOffset) / 16f;
        for (Rect2i rect : mask.rectangularize()) {
            final BlockElementFace frontFace = new BlockElementFace(null, tintIndex, textureKey, new BlockFaceUV(new float[]{
                uOffset + uScale * rect.getX(),
                vOffset + vScale * rect.getY(),
                uOffset + uScale * (rect.getX() + rect.getWidth()),
                vOffset + vScale * (rect.getY() + rect.getHeight())
            }, 0));
            elements.add(new BlockElement(
                new Vector3f(rect.getX(), 16f - rect.getY() - rect.getHeight(), minZ),
                new Vector3f(rect.getX() + rect.getWidth(), 16f - rect.getY(), maxZ),
                Map.of(Direction.SOUTH, frontFace),
                null, true
            ));
        };
        return elements;
    };

    private List<BlockElement> getExtrudedSideElements(Mask mask, float minZ, float maxZ) {
        // final float width = 16f; // Holdover from copying from ItemModelGenerator
        // final float height = 16f;
        final List<BlockElement> elements = Lists.newArrayList();

        for (ExtrudedModelGenerator.Span span : getSpans(mask)) {
            float x1 = 0f;
            float y1 = 0f;
            float x2 = 0f;
            float y2 = 0f;
            float u1 = 0f;
            float u2 = 0f;
            float v1 = 0f;
            float v2 = 0f;
            // final float widthScale = 16f / width;
            // final float heightScale = 16f / height;
            final float spanMin = (float)span.getMin();
            final float spanMax = (float)span.getMax();
            final float spanchor = (float)span.getAnchor();
            final ExtrudedModelGenerator.SpanFacing spanFacing = span.getFacing();

            switch (spanFacing) {
                case UP:
                    u1 = spanMin;
                    x1 = spanMin;
                    x2 = u2 = spanMax + 1f;
                    v1 = maxZ;
                    y1 = spanchor;
                    y2 = spanchor;
                    v2 = minZ;
                    break;
                case DOWN:
                    v1 = minZ;
                    v2 = maxZ;
                    u1 = spanMin;
                    x1 = spanMin;
                    x2 = u2 = spanMax + 1f;
                    y1 = spanchor + 1f;
                    y2 = spanchor + 1f;
                    break;
                case LEFT:
                    u1 = maxZ;
                    x1 = spanchor;
                    x2 = spanchor;
                    u2 = minZ;
                    v2 = spanMin;
                    y1 = spanMin;
                    y2 = v1 = spanMax + 1f;
                    break;
                case RIGHT:
                    u1 = minZ;
                    u2 = maxZ;
                    x1 = spanchor + 1f;
                    x2 = spanchor + 1f;
                    v2 = spanMin;
                    y1 = spanMin;
                    y2 = v1 = spanMax + 1f;
            }

            // x1 *= widthScale;
            // x2 *= widthScale;
            // y1 *= heightScale;
            // y2 *= heightScale;
            y1 = 16f - y1;
            y2 = 16f - y2;
            // u1 *= widthScale;
            // u2 *= widthScale;
            // v1 *= heightScale;
            // v2 *= heightScale;
            u1 = MathsHelper.floorMod(u1, 16f);
            u2 = MathsHelper.floorMod(u2, 16f);
            v1 = MathsHelper.floorMod(v1, 16f);
            v2 = MathsHelper.floorMod(v2, 16f);

            final Map<Direction, BlockElementFace> elementFaces = Maps.newHashMap();
            elementFaces.put(spanFacing.direction, new BlockElementFace(null, 0, SIDE_TEXTURE_KEY, new BlockFaceUV(new float[]{u1, v1, u2, v2}, 0)));
            switch (spanFacing) {
                case UP:
                    elements.add(new BlockElement(new Vector3f(x1, y1, minZ), new Vector3f(x2, y1, maxZ), elementFaces, null, true));
                    break;
                case DOWN:
                    elements.add(new BlockElement(new Vector3f(x1, y2, minZ), new Vector3f(x2, y2, maxZ), elementFaces, null, true));
                    break;
                case LEFT:
                    elements.add(new BlockElement(new Vector3f(x1, y1, minZ), new Vector3f(x1, y2, maxZ), elementFaces, null, true));
                    break;
                case RIGHT:
                    elements.add(new BlockElement(new Vector3f(x2, y1, minZ), new Vector3f(x2, y2, maxZ), elementFaces, null, true));
            }
        }

        return elements;
    }

    private List<ExtrudedModelGenerator.Span> getSpans(Mask mask) {
        final List<ExtrudedModelGenerator.Span> spans = Lists.newArrayList();

        for (int x = mask.minX(); x <= mask.maxX(); x++) {
            for (int y = mask.minY(); y <= mask.maxY(); y++) {
                final boolean masked = mask.get(x, y);
                for (SpanFacing spanFacing : SpanFacing.values()) {
                    tryCreateOrExpandSpan(spanFacing, spans, mask, x, y, masked);
                };
            };
        };
        
        return spans;
    }

    private void tryCreateOrExpandSpan(ExtrudedModelGenerator.SpanFacing spanFacing, List<ExtrudedModelGenerator.Span> spans, Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
        if (spanFacing.shouldHaveFace(mask, pixelX, pixelY, pixelMasked)) createOrExpandSpan(spans, spanFacing, pixelX, pixelY);
    };

    private void createOrExpandSpan(List<ExtrudedModelGenerator.Span> spans, ExtrudedModelGenerator.SpanFacing spanFacing, int pixelX, int pixelY) {
        
        ExtrudedModelGenerator.Span span = null;

        final int anchor = spanFacing.isHorizontal() ? pixelY : pixelX;
        final int pos = spanFacing.isHorizontal() ? pixelX : pixelY;

        for (ExtrudedModelGenerator.Span existingSpan : spans) {
            if (existingSpan.getFacing() == spanFacing) {
                if (existingSpan.getAnchor() == anchor && existingSpan.canBeExpandedTo(pos)) { // unlike in Item Models, spans cannot cross gaps in the Mask
                    span = existingSpan;
                    break;
                };
            };
        };

        if (span == null) {
            spans.add(new ExtrudedModelGenerator.Span(spanFacing, pos, anchor));
        } else {
            span.expand(pos);
        };
    };

    @OnlyIn(Dist.CLIENT)
    static class Span {
        private final ExtrudedModelGenerator.SpanFacing facing;
        private int min;
        private int max;
        private final int anchor;

        private final int block; // Separate spans into blocks of 16 so we dont have to render multiple copies of one side texture per span. Front and back textures ignore this.

        public Span(ExtrudedModelGenerator.SpanFacing facing, int minMax, int anchor) {
            this.facing = facing;
            this.min = minMax;
            this.max = minMax;
            this.anchor = anchor;
            this.block = Math.floorDiv(minMax, 16);
        };

        public boolean canBeExpandedTo(int pos) { // Note this only works because we add pixels in order
            return pos + 1 >= min && pos - 1 <= max && Math.floorDiv(pos, 16) == block;
        };

        public void expand(int pos) {
            if (pos < min) {
                min = pos;
            } else if (pos > max) {
                max = pos;
            };
        };

        public ExtrudedModelGenerator.SpanFacing getFacing() {
            return this.facing;
        };

        public int getMin() {
            return this.min;
        };

        public int getMax() {
            return this.max;
        };

        public int getAnchor() {
            return this.anchor;
        };
    };

    @OnlyIn(Dist.CLIENT)
    static enum SpanFacing {
        UP(Direction.UP) {

            @Override
            public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
                return !mask.get(pixelX, pixelY - 1) && pixelMasked;
            };
        },
        DOWN(Direction.DOWN) {

            @Override
            public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
                return !mask.get(pixelX, pixelY + 1) && pixelMasked;
            };
        },
        LEFT(Direction.EAST) {

            @Override
            public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
                return !mask.get(pixelX - 1, pixelY) && pixelMasked;
            };
        },
        RIGHT(Direction.WEST) {

            @Override
            public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
                return !mask.get(pixelX + 1, pixelY) && pixelMasked;
            };
        },
        // FRONT(Direction.SOUTH) {

        //     @Override
        //     public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
        //         return pixelMasked;
        //     };
        // },
        // BACK(Direction.NORTH) {

        //     @Override
        //     public boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked) {
        //         return pixelMasked;
        //     };
        // },
        ;

        public final Direction direction;

        private SpanFacing(Direction direction) {
            this.direction = direction;
        };

        public abstract boolean shouldHaveFace(Mask mask, int pixelX, int pixelY, boolean pixelMasked);

        public boolean isHorizontal() {
            return this == DOWN || this == UP;
        };
    };
};
