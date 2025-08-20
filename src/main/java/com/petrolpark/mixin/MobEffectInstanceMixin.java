package com.petrolpark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.petrolpark.shadereffect.ClientEffectHandler;
import com.petrolpark.shadereffect.IShaderEffect;
import com.petrolpark.shadereffect.packet.InitShaderPacket;
import com.petrolpark.shadereffect.packet.SyncInitialDurationPacket;
import com.petrolpark.util.mixininterfaces.IMobEffectInstanceMixin;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceMixin, Comparable<MobEffectInstance>{
    @Shadow
    private int duration;

    @Shadow
    private Holder<MobEffect> effect;

    private int initialDuration;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("RETURN"))
    private void onInitialize(CallbackInfo ignored) {
        initialDuration = duration;
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void inEffectAdded(LivingEntity entity, CallbackInfo ignored) {
        if (entity instanceof ServerPlayer player && effect.value() instanceof IShaderEffect) {
            String effectId = BuiltInRegistries.MOB_EFFECT.getKey(this.effect.value()).toString();
            PacketDistributor.sendToPlayer(player,
                    new SyncInitialDurationPacket(this.initialDuration, effectId),
                    new InitShaderPacket(effectId));
        }
    }

    @ModifyReturnValue( method = "save", at = @At("RETURN") )
    private Tag saveData(Tag original) {
        CompoundTag nbt = (( CompoundTag ) original);
        nbt.putInt("initialDuration", this.getInitialDuration());
        return nbt;
    }

    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    private static MobEffectInstance loadData(MobEffectInstance original, CompoundTag nbt) {
        if (original != null) {
            (( IMobEffectInstanceMixin ) original).setInitialDuration(nbt.getInt("initialDuration"));
        }
        return original;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateUniforms() {
        float value = duration >= 0 && initialDuration > 0 ?
                (float) duration / initialDuration :
                0.5f;

        ClientEffectHandler.updateUniforms(value);
    }

    @Override
    public void setInitialDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }

    @Override
    public int getInitialDuration() {
        return initialDuration;
    }
}
