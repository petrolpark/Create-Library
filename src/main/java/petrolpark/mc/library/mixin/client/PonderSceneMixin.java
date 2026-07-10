package petrolpark.mc.library.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.resources.ResourceLocation;
import petrolpark.mc.library.core.client.ponder.WatchedPonderPacket;

@Mixin(PonderScene.class)
public class PonderSceneMixin {

    @Shadow
    private boolean finished;

    @Shadow
    private List<PonderInstruction> activeSchedule;

    @Shadow
    private ResourceLocation sceneId;
    
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "isEmpty"
        )
    )
    public void petrolpark$sendPacketOnceFinished(CallbackInfo ci) {
        if (activeSchedule.isEmpty() && !finished) CatnipServices.NETWORK.sendToServer(new WatchedPonderPacket(sceneId));
    };
};
