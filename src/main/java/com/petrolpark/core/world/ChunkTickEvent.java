package com.petrolpark.core.world;

import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.Event;

/**
 * Fired every tick for every loaded {@link LevelChunk}, on the Neo event bus on the server side only.
 */
public abstract class ChunkTickEvent extends Event {
    
    protected final LevelChunk chunk;
    protected final int randomTickSpeed;

    public ChunkTickEvent(LevelChunk chunk, int randomTickSpeed) {
        this.chunk = chunk;
        this.randomTickSpeed = randomTickSpeed;
    };

    public LevelChunk getChunk() {
        return chunk;
    };

    public int getRandomTickSpeed() {
        return randomTickSpeed;
    };

    public static class Pre extends ChunkTickEvent {

        public Pre(LevelChunk chunk, int randomTickSpeed) {
            super(chunk, randomTickSpeed);
        };

    };

    public static class Post extends ChunkTickEvent {

        public Post(LevelChunk chunk, int randomTickSpeed) {
            super(chunk, randomTickSpeed);
        };
    };

};
