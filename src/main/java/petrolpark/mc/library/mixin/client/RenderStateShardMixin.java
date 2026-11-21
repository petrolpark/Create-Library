package petrolpark.mc.library.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import petrolpark.mc.library.core.client.effectShaders.SharedTargetsHandler;

@Mixin( RenderStateShard.class)
public class RenderStateShardMixin {
    @ModifyVariable(method = "<init>",
            at = @At( "HEAD" ),
            ordinal = 0,
            argsOnly = true
    )
    private static Runnable petrolpark$modifySetupState(Runnable setupState,
                                                        String name) {
        return () -> {
            setupState.run();
            if (SharedTargetsHandler.shouldBeLoaded(name)) {
                SharedTargetsHandler.getTarget(name).bindWrite(true);
            }
        };
    }

    @ModifyVariable(method = "<init>",
            at = @At( "HEAD" ),
            ordinal = 1,
            argsOnly = true
    )
    private static Runnable petrolpark$modifyClearState(Runnable clearState,
                                                        String name) {
        return () -> {
            clearState.run();
            if (SharedTargetsHandler.shouldBeLoaded(name)) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            }
        };
    }
}
