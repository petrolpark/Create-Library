package petrolpark.mc.library.util;

import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.instruction.ReplaceBlocksInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PonderHelper {
    
    public static void swapBlockState(SceneBuilder scene, BlockPos pos, BlockState state, boolean spawnParticles) {
        scene.addInstruction(new ReplaceBlocksInstruction(scene.getScene().getSceneBuildingUtil().select().position(pos), s -> BlockHelper.copyAll(state, scene.getScene().getWorld().getBlockState(pos)), true, spawnParticles));
    };
};
