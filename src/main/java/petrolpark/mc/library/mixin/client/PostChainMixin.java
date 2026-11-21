package petrolpark.mc.library.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.ChainedJsonException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import petrolpark.mc.library.core.client.effectShaders.SharedTargetsHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mixin(PostChain.class)
public class PostChainMixin {
    @Shadow
    @Final
    private RenderTarget screenTarget;

    @Shadow
    @Final
    private Map<String, RenderTarget> customRenderTargets;

    @Shadow
    private int screenWidth;

    @Shadow
    private int screenHeight;

    @Shadow
    @Final
    private List<RenderTarget> fullSizedTargets;

    @Inject(
            method = "addTempTarget",
            at = @At( "HEAD" ),
            cancellable = true
    )
    public void petrolpark$addSharedTargets(String name, int width, int height, CallbackInfo ci) {
        if (name.startsWith("$")) {
            RenderTarget rendertarget = SharedTargetsHandler.getTarget(name.substring(1));

            if (this.screenTarget.isStencilEnabled()) {
                rendertarget.enableStencil();
            }

            this.customRenderTargets.put(name.substring(1), rendertarget);
            if (width == this.screenWidth && height == this.screenHeight) {
                this.fullSizedTargets.add(rendertarget);
            }
            ci.cancel();
        }
    }

    @Inject(
            method = "getRenderTarget",
            at = @At("HEAD"),
            cancellable = true )
    public void petrolpark$getSharedTarget(String target, CallbackInfoReturnable<RenderTarget> cir) {
        if (target != null && target.startsWith("$")) {
            cir.setReturnValue(this.customRenderTargets.get(target.substring(1)));
            cir.cancel();
        }
    }

    @Inject(method = "parsePassNode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/PostChain;getRenderTarget(Ljava/lang/String;)Lcom/mojang/blaze3d/pipeline/RenderTarget;",
                    ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void petrolpark$preventWriting(TextureManager textureManager, JsonElement json, CallbackInfo ci, JsonObject jsonobject, String s, String s1, String s2) throws IOException {
        if (SharedTargetsHandler.hasTarget(s2)) {
            throw new ChainedJsonException("Only auxiliary and input buffers can be a shared render target.");
        }
    }
}
