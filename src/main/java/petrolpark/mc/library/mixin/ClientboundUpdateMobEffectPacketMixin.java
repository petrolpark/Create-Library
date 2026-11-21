package petrolpark.mc.library.mixin;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import petrolpark.mc.library.util.mixininterfaces.IClientboundUpdateMobEffectPacketMixin;
import petrolpark.mc.library.util.mixininterfaces.IMobEffectInstanceMixin;

@Mixin( ClientboundUpdateMobEffectPacket.class)
public class ClientboundUpdateMobEffectPacketMixin implements IClientboundUpdateMobEffectPacketMixin {
    @Unique
    int petrolpark$totalDuration;

    @Inject(
            method = "<init>(ILnet/minecraft/world/effect/MobEffectInstance;Z)V",
            at = @At( "TAIL" )
    )
    public void petrolpark$init(int entityId, MobEffectInstance effect, boolean blend, CallbackInfo ci) {
        this.petrolpark$totalDuration = (( IMobEffectInstanceMixin ) effect).petrolpark$getTotalDuration();
    }

    @Inject(
            method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V",
            at = @At("TAIL")
    )
    public void petrolpark$init(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        this.petrolpark$totalDuration = buffer.readInt();
    }

    @Inject(
            method = "write(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V",
            at = @At("TAIL")
    )
    public void petrolpark$write(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeInt(this.petrolpark$totalDuration);
    }

    @Override
    public int petrolpark$getTotalDuration() {
        return petrolpark$totalDuration;
    }
}
