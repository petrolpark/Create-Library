package petrolpark.mc.library.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.Rect2i;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A set of "enabled" pixels in a global 2D grid.
 */
public class Mask implements Cloneable {

    public static final Codec<Mask> FRIENDLY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("x_offset", 0).forGetter(Mask::minX),
        Codec.INT.optionalFieldOf("y_offset", 0).forGetter(Mask::minY),
        Codec.STRING.listOf().fieldOf("pattern").forGetter(Mask::rowStrings)
    ).apply(instance, Mask::fromRowStrings));

    public static final Codec<Mask> friendlyCodecSized(int maxWidth, int maxHeight) {
        return Codec.string(0, maxWidth)
            .listOf(0, maxHeight)
            .xmap(strings -> fromRowStrings(0, 0, strings), Mask::rowStrings);
    };

    private BitSet bits = new BitSet();

    private int xOffset;
    private int yOffset;
    private int width;
    private int height;

    private Mask(int xOffset, int yOffset, int width, int height) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
        bits = new BitSet(width * height);
    };

    private int index(int x, int y) {
        return (y - yOffset) * width + (x - xOffset);
    };

    private boolean inBounds(int x, int y) {
        return x >= xOffset && x < xOffset + width && y >= yOffset && y < yOffset + height;
    };

    /**
     * Does not change this {@link Mask}, but expands the BitSet internally to cover the space occupied by the given {@link Mask}.
     * @see Mask#trim() Inverse
     */
    public void cover(Mask other) {
        cover(other.minX(), other.minY(), other.maxX(), other.maxY());
    };

    /**
     * Does not change this {@link Mask}, but expands the BitSet internally to cover at least the given space.
     * @see Mask#trim()
     */
    public void cover(int minX, int minY, int maxX, int maxY) {
        rebase(Math.min(minX(), minX), Math.min(minY(), minY), Math.max(maxX(), maxX), Math.max(maxY(), maxY));
    };

    /**
     * Scales this {@link Mask} internally, potentially removing set pixels.
     * @param newMinX
     * @param newMinY
     * @param newMaxX
     * @param newMaxY
     */
    private void rebase(int newMinX, int newMinY, int newMaxX, int newMaxY) {
        if (newMinX == xOffset && newMinY == yOffset && newMaxX == maxX() && newMaxY == maxY()) return;

        final int newWidth  = newMaxX - newMinX + 1;
        final int newHeight = newMaxY - newMinY + 1;
        final BitSet newBits = new BitSet(newWidth * newHeight);

        for (int y = newMinY; y <= newMaxY; y++) {
            for (int x = newMinX; x <= newMaxX; x++) {
                if (get(x, y)) {
                    int index = (y - newMinY) * newWidth + (x - newMinX);
                    newBits.set(index);
                };
            };
        };

        this.width = newWidth;
        this.height = newHeight;
        this.xOffset = newMinX;
        this.yOffset = newMinY;
        this.bits = newBits;
    };

    /**
     * Does not change this {@link Mask}, but shrinks the BitSet internally as much as possible.
     */
    private void trim() {
        if (bits.isEmpty()) {
            width = height = 0;
            xOffset = yOffset = 0;
            return;
        };

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            final int x = i % width;
            final int y = i / width;

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        };

        if (minX == 0 && minY == 0 && maxX == width - 1 && maxY == height - 1) return;
        
        rebase(
            xOffset + minX,
            yOffset + minY,
            xOffset + maxX,
            yOffset + maxY
        );
    };

    @FunctionalInterface
    public static interface Combination { boolean combine(boolean a, boolean b); };

    /**
     * Get the internal x origin of this {@link Mask}.
     * This is at most but not necessarily equal to the x coordinate of the leftmost set pixel.
     * To ensure equality, call {@link Mask#trim()}.
     */
    public int minX() { 
        return xOffset; 
    };

    /**
     * Get the internal y origin of this {@link Mask}.
     * This is at most but not necessarily equal to the y coordinate of the topmost set pixel.
     * To ensure equality, call {@link Mask#trim()}.
     */
    public int minY() { 
        return yOffset;
    };

    /**
     * Get the internal x maximum of this {@link Mask}.
     * This is at most but not necessarily equal to the x coordinate of the rightmost set pixel.
     * To ensure equality, call {@link Mask#trim()}.
     */
    public int maxX() {
        return xOffset + width - 1;
    };

    /**
     * Get the internal y maximum of this {@link Mask}.
     * This is at most but not necessarily equal to the y coordinate of the bottommost set pixel.
     * To ensure equality, call {@link Mask#trim()}.
     */
    public int maxY() {
        return yOffset + height - 1;
    };

    public boolean get(int x, int y) {
        return inBounds(x, y) && bits.get(index(x, y));
    };

    /**
     * {@code true} if any pixels are set in the given region
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public boolean getRegion(int minX, int minY, int maxX, int maxY) {
        final int startY = Math.max(minY, yOffset);
        final int endY = Math.min(maxY, yOffset + height - 1);

        final int startX = Math.max(minX, xOffset);
        final int endX = Math.min(maxX, xOffset + width - 1);

        if (startX > endX || startY > endY) return false;

        for (int y = startY; y <= endY; y++) {
            int rowStart = (y - yOffset) * width;
            int from = rowStart + (startX - xOffset);
            int to = rowStart + (endX - xOffset) + 1;

            int bit = bits.nextSetBit(from);
            if (bit >= from && bit < to) {
                return true;
            };
        };

        return false;
    }

    private void setUnchecked(int x, int y, boolean value) {
        bits.set(index(x, y), value);
    };

    /**
     * Sets the given pixel in this {@link Mask}.
     * @param x
     * @param y
     */
    public void set(int x, int y) {
        if (!inBounds(x, y)) cover(x, y, x, y);
        setUnchecked(x, y, true);
    };

    /**
     * Removes the given pixel from this {@link Mask}.
     * @param x
     * @param y
     * @see Mask#trim() Make the Mask internally as small as possible after doing this
     */
    public void clear(int x, int y) {
        if (inBounds(x, y)) bits.clear(index(x, y));
    };

    /**
     * Moves every pixel in this {@link Mask}.
     * @param dX
     * @param dY
     */
    public void move(int dX, int dY) {
        xOffset = xOffset + dX;
        yOffset = yOffset + dY;
    };

    /**
     * Perform a binary operation on this {@link Mask} based on the contents of the given {@link Mask}.
     * This modifies this {@link Mask}, but not the given one.
     * @param mask
     * @param combination
     * @see Mask#or(Mask)
     * @see Mask#and(Mask)
     * @see Mask#andNot(Mask)
     */
    public void combine(Mask mask, Combination combination) {
        cover(mask);
        for (int x = minX(); x <= maxX(); x++) {
            for (int y = minY(); y <= maxY(); y++) {
                setUnchecked(x, y, combination.combine(get(x, y), mask.get(x, y)));
            };
        };
    };

    /**
     * Effectively add the given {@link Mask} to this one.
     * This modifies this {@link Mask}, but not the given one.
     * @param mask
     * @see Mask#combine(Mask, Combination)
     * @see Mask#and(Mask)
     * @see Mask#andNot(Mask)
     */
    public void or(Mask mask) {
        combine(mask, (a, b) -> a || b);
        // no need to trim
    };

    /**
     * Intersect this {@link Mask} with the given {@link Mask}.
     * This modifies this {@link Mask}, but not the given one.
     * @param mask
     * @see Mask#combine(Mask, Combination)
     * @see Mask#or(Mask)
     * @see Mask#andNot(Mask)
     */
    public void and(Mask mask) {
        combine(mask, (a, b) -> a && b);
        trim();
    };

    /**
     * Subtract the given {@link Mask} from this one.
     * This modifies this {@link Mask}, but not the given one.
     * @param mask
     * @see Mask#combine(Mask, Combination)
     * @see Mask#or(Mask)
     * @see Mask#and(Mask)
     */
    public void andNot(Mask mask) {
        combine(mask, (a, b) -> a && !b);
        trim();
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Mask mask) {
            final int minX = Math.min(minX(), mask.minX());
            final int minY = Math.min(minY(), mask.minY());
            final int maxX = Math.max(maxX(), mask.maxX());
            final int maxY = Math.max(maxY(), mask.maxY());
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    if (get(x, y) != mask.get(x, y)) return false;
                };
            };
            return true;
        };
        return false;
    };

    @Override
    public Mask clone() {
        final Mask clone = new Mask(xOffset, yOffset, width, height);
        clone.bits = (BitSet)bits.clone();
        return clone;
    };

    /**
     * Serialize this {@link Mask}. Set pixels will be represented with a {@code .} and unset pixels with a space.
     */
    public List<String> rowStrings() {
        final List<String> rowStrings = new ArrayList<>(height);
        for (int y = minY(); y <= maxY(); y++) {
            String rowString = "";
            for (int x = minX(); x <= maxX(); x++) {
                rowString += get(x, y) ? "." : " ";
            };
            rowStrings.add(rowString);
        };
        return rowStrings;
    };

    /**
     * Get a <em>new</em> {@link Mask} by splitting this Mask into {@code factor} by {@code factor} blocks
     * and setting pixels in that Mask if any pixels in the corresponding block in this Mask are set.
     * @param factor
     */
    public Mask downsample(int factor) {
        if (factor <= 0) throw new IllegalArgumentException("factor must be > 0");
    
        final Mask result = Mask.rect(0, 0, 0, 0);

        if (bits.isEmpty()) return result;

        int newMinX = Math.floorDiv(xOffset, factor);
        int newMinY = Math.floorDiv(yOffset, factor);
        int newMaxX = Math.floorDiv(xOffset + width - 1, factor);
        int newMaxY = Math.floorDiv(yOffset + height - 1, factor);

        for (int y = newMinY; y <= newMaxY; y++) {
            int srcY0 = y * factor;
            int srcY1 = srcY0 + factor - 1;

            for (int x = newMinX; x <= newMaxX; x++) {
                int srcX0 = x * factor;
                int srcX1 = srcX0 + factor - 1;

                if (getRegion(srcX0, srcY0, srcX1, srcY1)) result.set(x, y);
            };
        };

        return result;
    };

    /**
     * Deserialize a {@link Mask}. Spaces represent unset pixels and any other symbols represent set pixels.
     * @param xOffset Horizontal offset to apply to the whole {@link Mask}. The same effect would be achieved by including {@code xOffset} spaces at the front of every line, but this field can also work for negative offsets.
     * @param yOffset Vertical offset to apply to the whole {@link Mask}. The same effect would be achieved by including {@code yOffset} empty lines at the start, but this field can also work for negative offsets.
     * @param rowStrings Must have all Strings the same length
     */
    public static final Mask fromRowStrings(int xOffset, int yOffset, List<String> rowStrings) {
        if (rowStrings.isEmpty()) return new Mask(xOffset, yOffset, 0, 0);
        final int width = rowStrings.get(0).length();
        final Mask mask = new Mask(xOffset, yOffset, width, rowStrings.size());
        int relY = 0;
        for (String rowString : rowStrings) {
            if (rowString.length() != width) throw new IllegalArgumentException("Rows must all be same width");
            for (int relX = 0; relX < width; relX++) {
                if (rowString.charAt(relX) != ' ') mask.set(xOffset + relX, yOffset + relY);
            };
            relY++;
        };
        return mask;
    };

    /**
     * Convert this {@link Mask} to a List of {@link Rect2i rectangles}.
     * More horizontal rectangles are preferred.
     * @return List of {@link Rect2i rectangles} covering every pixel in this {@link Mask} exactly once.
     */
    @OnlyIn(Dist.CLIENT)
    public List<Rect2i> rectangularize() {
        final List<Rect2i> rectangles = new ArrayList<>();
        if (bits.isEmpty()) return rectangles;

        final BitSet remainingBits = (BitSet)bits.clone();

        for (int y = 0; y < height; y++) {
            final int rowBase = y * width;

            for (int idx = remainingBits.nextSetBit(rowBase);
                idx >= rowBase && idx < rowBase + width;
                idx = remainingBits.nextSetBit(idx + 1)) {

                final int startX = idx - rowBase;

                // Extend horizontally
                int endX = startX;
                while (endX + 1 < width &&
                    remainingBits.get(rowBase + endX + 1)) {
                    endX++;
                };

                // Extend vertically
                int endY = y;
                outer: while (endY + 1 < height) {
                    int nextRow = (endY + 1) * width;
                    for (int x = startX; x <= endX; x++) {
                        if (!remainingBits.get(nextRow + x)) {
                            break outer;
                        }
                    }
                    endY++;
                };

                // Mark consumed cells
                for (int yy = y; yy <= endY; yy++) {
                    int base = yy * width;
                    remainingBits.clear(base + startX, base + endX + 1);
                };

                rectangles.add(new Rect2i(
                    xOffset + startX,
                    yOffset + y,
                    endX - startX + 1,
                    endY - y + 1
                ));
            };
        };

        return rectangles;

    };

    /**
     * Construct a {@link Mask} at the given position of the given size.
     * @param x
     * @param y
     * @param width
     * @param height
     * @see Mask#fromTo(int, int, int, int)
     */
    public static final Mask rect(int x, int y, int width, int height) {
        if (width < 0 || height < 0) throw new IllegalArgumentException("Cannot have negative width or height");
        final Mask mask = new Mask(x, y, width, height);
        mask.bits.set(0, mask.bits.length());
        return mask;
    };

    /**
     * Construct a {@link Mask} with the given upper and lower corners.
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @see Mask#rect(int, int, int, int)
     */
    public static final Mask fromTo(int fromX, int fromY, int toX, int toY) {
        return rect(fromX, fromY, toX - fromX, toY - fromY);
    };

};
