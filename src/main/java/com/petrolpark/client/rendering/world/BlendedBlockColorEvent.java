package com.petrolpark.client.rendering.world;

import com.petrolpark.util.ColorHelper;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * {@link BlockColor}s are called after Biome blending has been done.
 * This event is fired on the {@link NeoForge#EVENT_BUS} on the client side to allow Block colors to be modified before they are blended.
 * <p>The result of this event is cached but can be reset with {@link ColorHelper#refreshChunkColors(ChunkPos)}.</p>
 * <p>This event will be fired many times for every block in a chunk, so it is recommended to cache intermediate values.</p>
 */
@OnlyIn(Dist.CLIENT)
public class BlendedBlockColorEvent extends Event {
    
    protected final ClientLevel level;
    protected final BlockPos pos;
    protected final Biome biome;
    protected final ColorResolver colorResolver;

    protected int color;

    public BlendedBlockColorEvent(ClientLevel level, BlockPos pos, Biome biome, ColorResolver colorResolver, int color) {
        this.level = level;
        this.pos = pos;
        this.biome = biome;
        this.colorResolver = colorResolver;
        this.color = color;
    };

    public ClientLevel getLevel() {
        return level;
    };

    public BlockPos getPos() {
        return pos;
    };

    public Biome getBiome() {
        return biome;
    };

    public ColorResolver getColorResolver() {
        return colorResolver;
    };

    public int getColor() {
        return color;
    };

    public void setColor(int color) {
        this.color = color;
    };


};
