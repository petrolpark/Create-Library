package petrolpark.mc.library.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
    
    @Invoker("onChunkReadyToSend")
    public void invokeOnChunkReadyToSend(LevelChunk chunk);
};
