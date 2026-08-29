package petrolpark.mc.library.mixin.client;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedMobEffects;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow
    private double xpos;
    @Shadow
    private double ypos;
    @Shadow
    private double accumulatedDX;
    @Shadow
    private double accumulatedDY;

    @Shadow
    public abstract boolean isMouseGrabbed();

    @Unique
    private double petrolpark$swayX;
    @Unique
    private double petrolpark$swayY;

    @Inject(
        method = "handleAccumulatedMovement",
        at = @At(
            value = "INVOKE",
            target = "isWindowActive"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void petrolpark$drunkenMouse(CallbackInfo ci, double time, double timeSinceLastMovement) {
        if (!SharedFeatureFlag.INEBRIATION.enabled()) return;
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        final MobEffectInstance instance = player.getEffect(SharedMobEffects.INEBRIATION);
        if (instance == null) return;

        final double swayDecay = 0.8d + Math.min(0.19d, instance.getAmplifier() * 0.025f);

        petrolpark$swayX = petrolpark$swayX * swayDecay + accumulatedDX * 0.4d;
        petrolpark$swayY = petrolpark$swayY * swayDecay + accumulatedDY * 0.4d;

        accumulatedDX += petrolpark$swayX;
        accumulatedDY += petrolpark$swayY;

        if (!isMouseGrabbed()) {
            final int curXpos = (int) Math.round(xpos);
            final int curYpos = (int) Math.round(ypos);
            final int newXpos = (int) Math.round(xpos + petrolpark$swayX);
            final int newYpos = (int) Math.round(ypos + petrolpark$swayY);

            if (newXpos != curXpos || newYpos != curYpos) {
                final Window window = Minecraft.getInstance().getWindow();
                if (newXpos >= 0 && newXpos <= window.getScreenWidth() && newYpos >= 0 && newYpos <= window.getScreenHeight()) {
                    xpos = newXpos;
                    ypos = newYpos;
                    GLFW.glfwSetCursorPos(window.getWindow(), xpos, ypos);
                };
            };
        };
    };
};
