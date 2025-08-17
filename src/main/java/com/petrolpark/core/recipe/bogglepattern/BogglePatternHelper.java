package com.petrolpark.core.recipe.bogglepattern;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Rotation;

public class BogglePatternHelper {

    public static final Codec<Integer> SHORT_CODEC = Codec.SHORT.xmap(BogglePatternHelper::fromShort, BogglePatternHelper::asShort);
    public static final StreamCodec<ByteBuf, Integer> SHORT_STREAM_CODEC = ByteBufCodecs.SHORT.map(BogglePatternHelper::fromShort, BogglePatternHelper::asShort);
    /*
     *  y x0  1  2  3
     *  0  00 01 02 03
     *  1  04 05 06 07
     *  2  08 09 10 11
     *  3  12 13 14 15
     */

    /**
     * The index if the pattern is rotated 90 degrees clockwise
     */
    public static final int[] ROTATED_90 = new int[]{3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12};
    /**
     * The index if the pattern is rotated 90 degrees anticlockwise
     */
    public static final int[] ROTATED_270 = new int[]{12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3};

    /**
     * The index if the pattern is flipped over the north-south axis
     */
    public static final int[] FLIPPED = new int[]{3, 2, 1, 0, 7, 6, 5, 4, 11, 10, 9, 8, 15, 14, 13, 12};

    /**
     * Set a position in a binary matrix to 1.
     * @param binaryMatrix The binary matrix to modify
     * @param index The index of the element to set
     * @return A new binary matrix with the punched hole
     */
    public static final int set1(int binaryMatrix, int index) {
        return binaryMatrix | 1 << index;
    };

    /**
     * Punch a hole in a pattern.
     * @param binaryMatrix The pattern to punch
     * @param x x-coordinate of the hole to punch
     * @param y y-coordinate of the hole to punch
     * @return A new pattern with the punched hole
     */
    public static final int set1(int binaryMatrix, int x, int y) {
        return set1(binaryMatrix, getIndex(x, y));
    };

    public static final boolean is1(int binaryMatrix, int x, int y) {
        return is1(binaryMatrix, getIndex(x, y));
    };

    public static final boolean is1(int binaryMatrix, int index) {
        validateIndex(index);
        return (binaryMatrix & (1 << index)) != 0;
    };

    public static final int getIndex(int x, int y) {
        return x + (4 * y);
    };

    public static final int rotate(int index, Rotation rotation) {
        validateIndex(index);
        for (int i = 0; i < rotation.ordinal(); i++) {
            index = ROTATED_270[index];
        };
        return index;
    };

    public static final int flip(int index) {
        validateIndex(index);
        return FLIPPED[index];
    };

    public static final int rotateMatrix(int binaryMatrix, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++) {
            binaryMatrix = (
                ((binaryMatrix & (1 <<  0)) << 12) | ((binaryMatrix & (1 <<  1)) <<  8) |
                ((binaryMatrix & (1 <<  2)) <<  4) | ((binaryMatrix & (1 <<  3)) <<  0) |
                ((binaryMatrix & (1 <<  4)) <<  8) | ((binaryMatrix & (1 <<  5)) <<  4) |
                ((binaryMatrix & (1 <<  6)) <<  0) | ((binaryMatrix & (1 <<  7)) >>  4) |
                ((binaryMatrix & (1 <<  8)) <<  4) | ((binaryMatrix & (1 <<  9)) <<  0) |
                ((binaryMatrix & (1 << 10)) >>  4) | ((binaryMatrix & (1 << 11)) >>  8) |
                ((binaryMatrix & (1 << 12)) <<  0) | ((binaryMatrix & (1 << 13)) >>  4) |
                ((binaryMatrix & (1 << 14)) >>  8) | ((binaryMatrix & (1 << 15)) >> 12)
            );
        };
        return binaryMatrix;
    };

    public static final int getWidth(int binaryMatrix) {
        if (binaryMatrix == 0) return 0;
        for (int x = 3; x >= 1; x--) {
            for (int y = 0; y < 4; y++) {
                if (is1(binaryMatrix, x, y)) return x;
            };
        };
        return 0;
    };

    public static final int getHeight(int binaryMatrix) {
        if (binaryMatrix == 0) return 0;
        for (int y = 3; y >=1 ; y--) {
            for (int x = 0; x < 4; x++) {
                if (is1(binaryMatrix, x, y)) return y;
            };
        };
        return 0;
    };

    public static final void validateIndex(int index) {
        if (index < 0 || index >= 16) throw new IllegalArgumentException("Indices in a 4x4 binary matrix go from 0 to 15");
    };

    public static final short asShort(int binaryMatrix) {
        return (short)(binaryMatrix - Short.MAX_VALUE);
    };

    public static final int fromShort(short binaryMatrixShort) {
        return (int)Short.MAX_VALUE + (int)binaryMatrixShort;  
    };

    public static final String[] format(int bogglePattern) {
        String[] lines = new String[4];
        for (int row = 0; row < 4; row++) {
            String line = "";
            for (int column = 0; column < 4; column++) {
                line += is1(bogglePattern, column, row) ? "█" : "▒";
            };
            lines[row] = line;
        };
        return lines;
    };
};
