package petrolpark.mc.library.core.client.effectShaders;

import com.mojang.blaze3d.pipeline.TextureTarget;

public class NonDestructibleTextureTarget extends TextureTarget {
    public NonDestructibleTextureTarget(int width, int height, boolean useDepth, boolean clearError) {
        super(width, height, useDepth, clearError);
    }

    @Override
    public void resize(int width, int height, boolean clearError) {
        closeTexture();
        super.resize(width, height, clearError);
    }

    @Override
    public void destroyBuffers() {}

    public void closeTexture() {
        super.destroyBuffers();
    }
}
